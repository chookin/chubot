package cmri.utils.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteException;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zhuyin on 12/15/14.
 */
public abstract class MongoDAO<T> extends MongoHandler{
    private final String collectionName;

    protected MongoDAO(String collectionName) {
        this.collectionName = collectionName;
    }

    protected abstract String getId(T entity);

    protected abstract BasicDBObject getBasicDBObject(T entity);

    public abstract T parse(DBObject dbObject);

    public String getCollectionName(){
        return this.collectionName;
    }

    public DBCollection getCollection(){
        return this.getDB().getCollection(collectionName);
    }

    /**
     * If exist a item equals that to save, then not save it.
     */
    public int saveIfNotExists(Collection<T> entities) {
        Collection<DBObject> dbObjs = entities.stream().map(this::getBasicDBObject).collect(Collectors.toList());
        return saveIfNotExists(this.collectionName, dbObjs, (o1, o2) -> {
            T t1 = parse(o1);
            T t2 = parse(o2);
            if(t1 != null) {
                return t1.equals(t2);
            }else {
                return t2 == null;
            }
        });
    }

    public void removeField(String field, DBObject query) {
        removeField(this.collectionName, field, query);
    }

    public List<T> find(Map<String, Object> kv) {
        return find(this.collectionName, kv).stream().map(this::parse).collect(Collectors.toList());
    }

    public List<T> find(DBObject ref) {
        return find(this.collectionName, ref).stream().map(this::parse).collect(Collectors.toList());
    }

    public T findFirst(DBObject ref) {
        return parse(findFirst(this.collectionName, ref));
    }

    /**
     * update by _id
     */
    public int update(T entity) {
        return updateOrInsert(this.collectionName, getBasicDBObject(entity));
    }

    /**
     * update by _id. Use BulkWriteOperation, remove the old and insert new.
     */
    public int updateOrInsert(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        return updateOrInsert(this.collectionName, entities.stream().map(this::getBasicDBObject).collect(Collectors.toList()));
    }
    /**
     * @throws BulkWriteException if duplicate key would happens.
     */
    public int insert(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        return insert(this.collectionName, entities.stream().map(this::getBasicDBObject).collect(Collectors.toList()));
    }
}

