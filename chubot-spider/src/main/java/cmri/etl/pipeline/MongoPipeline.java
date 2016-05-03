package cmri.etl.pipeline;

import cmri.etl.common.MapItem;
import cmri.etl.common.ResultItems;
import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.dao.MongoHandler;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 6/23/15.
 */
public class MongoPipeline implements Pipeline {
    private final Log LOG = LogFactory.getLog(MongoPipeline.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Set<BasicDBObject>> cache = new HashMap<>();
    private final Object master;
    private int cacheSize = 500;
    private final Map<String, Long> count = new HashMap<>();
    private final TimeoutWriteThread timeoutThread;

    /**
     * 一个{@link MongoPipeline}只处理一个主标识的
     *
     * @param master 主标识
     */
    public MongoPipeline(Object master) {
        this.master = master;

        timeoutThread = new TimeoutWriteThread();
        timeoutThread.setDaemon(true);
        timeoutThread.start();
    }

    public MongoPipeline setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        resultItems.getItems().forEach(this::add);
    }

    private void add(MapItem item) {
        Map<String, Object> map = item.toStringMap();
        Object collection = map.get("collection");
        if (!(collection instanceof String)) {
            return;
        }
        String collectionStr = (String) collection;
        BasicDBObject dbObj = parse(map);

        addToCache(collectionStr, dbObj);
        timeoutThread.update(collectionStr);
    }

    /**
     * 添加到缓存
     *
     * @param collection mongo数据集合名称
     * @param obj        mongo数据记录
     */
    private void addToCache(String collection, BasicDBObject obj) {
        lock.writeLock().lock();
        try {
            Set<BasicDBObject> my = cache.get(collection);
            if (my == null) {
                my = new HashSet<>();
                cache.put(collection, my);
            } else {
                // for hashset, If this set already contains the element, the add call leaves the set unchanged.
                my.remove(obj);
            }
            my.add(obj);
            if (my.size() >= cacheSize) {
                save(collection, my);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.readLock().lock();
        try {
            for (Map.Entry<String, Set<BasicDBObject>> entry : cache.entrySet()) {
                save(entry.getKey(), entry.getValue());
                getLogger().info("save " + count.get(entry.getKey()) + " items of " + (entry.getKey().equals(master) ? master : (master + " " + entry.getKey())));
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    protected int save(String collection, Collection<? extends DBObject> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        MongoHandler dao = MongoHandler.instance();
        int cnt = dao.updateOrInsert(collection, entities);
        if (count.containsKey(collection)) {
            count.put(collection, count.get(collection) + cnt);
        } else {
            count.put(collection, (long) cnt);
        }

        entities.clear();
        return cnt;
    }

    private BasicDBObject parse(Map<String, Object> m) {
        BasicDBObject dbObj = new BasicDBObject();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            if ("collection".equals(entry.getKey())) {
                continue;
            }
            dbObj.put(entry.getKey(), entry.getValue());
        }
        return dbObj;
    }

    /**
     * 用于写入那些长时间没有写入mongo的缓存数据.
     */
    private class TimeoutWriteThread extends Thread {
        /**
         * map of {集合名称,集合最后一次添加记录的时间}
         */
        private Map<String, Long> updateTime = new ConcurrentHashMap<>();
        /**
         * 超时时间,单位纳秒
         */
        private long timeout = 1000000L * 5000;// 5秒

        /**
         * 标记集合添加新的记录
         */
        public void update(String collection) {
            updateTime.put(collection, System.nanoTime());
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ThreadHelper.sleep(1000);
                    check();
                }
            } catch (Throwable e) {
                LOG.fatal(null, e);
            }
        }

        private void check(){
            long now = System.nanoTime();
            for (Map.Entry<String, Long> entry : updateTime.entrySet()) {
                if (now - entry.getValue() < timeout) {// 更有记录添加
                    continue;
                }
                lock.writeLock().lock();
                try {
                    Set<BasicDBObject> my = cache.get(entry.getKey());
                    if (my.isEmpty()) {
                        continue;
                    }
                    save(entry.getKey(), my);
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }
    }
}
