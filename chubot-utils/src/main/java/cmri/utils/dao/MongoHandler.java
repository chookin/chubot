package cmri.utils.dao;

import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.EqualComparator;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuyin on 6/24/15.
 */
public class MongoHandler {
    private static final Logger LOG = Logger.getLogger(MongoHandler.class);
    private static final String database = ConfigManager.get("mongo.database");
    private static final MongoHandler instance = new MongoHandler();
    private static final Lock lock = new ReentrantLock();

    /**
     * @return {@link MongoHandler}
     */
    public static MongoHandler instance(){
        return instance;
    }
    private MongoClient mongo = null;

    public DB getDB() {
        return this.getMongo().getDB(database);
    }

    public DBCollection getCollection(String collection){
        return this.getDB().getCollection(collection);
    }

    private static MongoClient createMongo() {
        MongoClient mongo = null;
        String host = ConfigManager.get("mongo.host");
        int port = ConfigManager.getInt("mongo.port");
        String userName = ConfigManager.get("mongo.user");

        try {
            String password = ConfigManager.get("mongo.password");
            if (userName == null || userName.isEmpty()) {
                mongo = new MongoClient(host, port);
            } else {
                ServerAddress serverAddress = new ServerAddress(host, port);
                MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, password.toCharArray());
                mongo = new MongoClient(serverAddress, Arrays.asList(credential));
            }
            mongo.isLocked();
        } catch (MongoTimeoutException mt) {
            LOG.error(mt.toString());
            LOG.warn("retry connect mongodb after 5 seconds");
            ThreadHelper.sleep(5000);
            return createMongo();
        } catch (UnknownHostException e) {
            LOG.fatal(null, e);
            System.exit(-1);
        }
        return mongo;
    }

    private MongoClient getMongo() {
        lock.lock();
        try {
            if (mongo == null) {
                mongo = createMongo();
            }
            return mongo;
        }finally {
            lock.unlock();
        }
    }

    /**
     * No need to call {@link #close()}.
     */
    @Deprecated
    public MongoHandler close() {
        return this;
    }

    public List<DBObject> find(String collection, Map<String, Object> kv) {
        QueryBuilder queryBuilder = new QueryBuilder();
        for (Map.Entry<String, Object> pair : kv.entrySet()) {
            queryBuilder.put(pair.getKey()).is(pair.getValue());
        }
        return find(collection, queryBuilder.get());
    }

    public List<DBObject> find(String collection, DBObject ref) {
        List<DBObject> entities = new ArrayList<>();
        DBCursor cursor = this.getCollection(collection).find(ref);
        while (cursor.hasNext()) {
            entities.add(cursor.next());
        }
        return entities;
    }

    public DBObject findFirst(String collection, DBObject ref) {
        DBCursor cursor = this.getCollection(collection).find(ref);
        if (cursor.hasNext()) {
            return cursor.next();
        }
        return null;
    }

    /**
     * If exist a item equals that to save, then not save it.
     */
    public int saveIfNotExists(String collection, Collection<? extends DBObject> entities, EqualComparator<DBObject> comparator) {
        int count = 0;
        DBCollection dbCollection = this.getCollection(collection);
        for (DBObject entity : entities) {
            DBObject saved = findFirst(collection, entity);
            if(!comparator.equals(entity, saved)){
                dbCollection.save(entity);
                ++count;
            }
        }
        return count;
    }

    /**
     * @throws BulkWriteException if duplicate key would happens.
     */
    public int insert(String collection, Collection<? extends DBObject> entities) {
        if (entities.isEmpty()) {
            return 0;
        }
        DBCollection dbCollection = this.getCollection(collection);
        BulkWriteOperation bulkop = dbCollection.initializeUnorderedBulkOperation();
        entities.forEach(bulkop::insert);
        BulkWriteResult result = bulkop.execute();
        LOG.trace("Mongo update: " + result);
        return result.getInsertedCount();
    }

    /**
     * update by '_id'
     */
    public int updateOrInsert(String collection, DBObject entity) {
        DBCollection dbCollection = this.getCollection(collection);
        WriteResult writeResult = dbCollection.update(new BasicDBObject("_id", entity.get("_id")), entity, true, false);
        return writeResult.getN();
    }

    /**
     * update by '_id'. Use BulkWriteOperation, remove the old and insert new.
     */
    public int updateOrInsert(String collection, Collection<? extends DBObject> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        DBCollection dbCollection = this.getCollection(collection);
        BulkWriteOperation bulkop = dbCollection.initializeOrderedBulkOperation();
        for (DBObject entity : entities) {
            if(entity.get("_id") == null){
                throw new RuntimeException("the entity doesn't has '_id' property");
            }
            bulkop.find(new BasicDBObject("_id", entity.get("_id"))).upsert().replaceOne(entity);
        }
        BulkWriteResult result = bulkop.execute();
        return result.getModifiedCount() + result.getUpserts().size();
    }

    public void dropField(String collection, String field, DBObject query) {
        DBCollection dbCollection = this.getCollection(collection);
        dbCollection.update(query, new BasicDBObject("$unset", new BasicDBObject(field, 1)), false, true);
    }
}
