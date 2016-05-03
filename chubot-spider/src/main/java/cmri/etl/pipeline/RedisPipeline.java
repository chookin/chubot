package cmri.etl.pipeline;

import cmri.etl.common.IdItem;
import cmri.etl.common.MapItem;
import cmri.etl.common.ResultItems;
import cmri.utils.dao.JedisFactory;
import cmri.utils.dao.JedisLock;
import cmri.utils.lang.SerializationHelper;
import org.apache.commons.lang3.Validate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhuyin on 5/18/15.
 *
 * 解决同一个物品属于多个分类的情境。
 以视频为例说明，一个视频可能同时属于00年代、言情、古装等多种分类，因此，最终存储的视频记录应该包含所有的这些分类信息。为此，使用RedisKindPipeline，以提高分类信息集成的快速性和可靠性。在存储采集到的视频记录之前，先查看redis里面是否已有该记录缓存，如果有，则合并记录，并将合并后的记录先后存储于redis、mongo，若没有，则直接先后存储到redis和mongo中。
 */
public abstract class RedisPipeline<T extends IdItem> implements Pipeline {
    private final Class<T> entityClass;
    private final JedisPool pool;
    private final Set<Pipeline> pipelines = new HashSet<>();

    public RedisPipeline(Class<T> entityClass){
        this(JedisFactory.getJedisPool(), entityClass);
    }

    public RedisPipeline(JedisPool jedisPool, Class<T> entityClass) {
        Validate.notNull(jedisPool, "para 'jedisPool' is null on RedisKindPipeline construction method");
        this.pool = jedisPool;
        this.entityClass = entityClass;
    }

    public RedisPipeline addPipeline(Pipeline pipeline){
        if(pipeline != null){
            this.pipelines.add(pipeline);
        }
        return this;
    }

    @Override
    public void process(ResultItems resultItems) {
        if(resultItems.isSkip()){
            return;
        }
        Collection<MapItem> items = resultItems.getItems();
        if(items.isEmpty()){
            return;
        }
        Jedis jedis = pool.getResource();
        try {
            for(MapItem item : items) {
                save(jedis, item);
            }
        } finally {
            pool.returnResource(jedis);
        }
        this.pipelines.forEach(pipeline -> pipeline.process(resultItems));
    }


    @Override
    public void close() throws IOException {
        for(Pipeline pipeline: this.pipelines) {
            pipeline.close();
        }
    }

    protected void save(Jedis jedis, MapItem item){
        if(!entityClass.isInstance(item)){
            return;
        }
        T myItem = entityClass.cast(item);
        String key = myItem.getId(); // must be IdItem, then we can get its key.
        JedisLock lock = new JedisLock(jedis, key);
        try {
            if(!lock.lock()){
                getLogger().error("Failed to get redis lock for "+key);
                return;
            }
            merge(myItem, jedis);
            jedis.set(key, SerializationHelper.serialize(myItem));
        }finally {
            lock.unlock();
        }
    }

    /**
     * 整合刚采集的物品信息和已缓存的物品信息
     * @param newOne 新抓取到的物品信息，该对象可以在本方法中被更新（例如，增加历史数据信息）
     * @param jedis Jedis instance
     */
    protected abstract void merge(T newOne, Jedis jedis);
}

