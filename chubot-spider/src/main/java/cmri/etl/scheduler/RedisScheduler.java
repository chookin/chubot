package cmri.etl.scheduler;

import cmri.etl.common.Request;
import cmri.etl.common.Task;
import cmri.utils.dao.JedisFactory;
import cmri.utils.lang.SerializationHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by zhuyin on 3/13/15.
 */
public class RedisScheduler implements MonitorableScheduler {
    private final JedisPool pool;

    public RedisScheduler(){
        this.pool = JedisFactory.getJedisPool();
    }

    private String getWaitSetKey(Task task) {
        return "w-" + task.name();
    }
    // If a spider use this scheduler quit with undone requests, then these undone requests are always in process. So, need to set timeout for request on the state of in process. If timeout, then back to wait state. But, it's not better, for request failed and push again...
    // private String getInProcessSetKey(Task task){return task.name();}

    private String getDoneSetKey(Task task) {
        return "d-" + task.name();
    }

    @Override
    public Scheduler push(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            String key = getKeyHex(request);
            if (jedis.sismember(getDoneSetKey(task), key)) {
                return this;
            }
            String str = SerializationHelper.serialize(request);
            jedis.sadd(getWaitSetKey(task), str);
        }finally {
            pool.returnResource(jedis);
        }
        return this;
    }

    @Override
    public Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            String str = jedis.spop(getWaitSetKey(task));
            Request request = SerializationHelper.deserialize(str);
            if (request == null) {
                return null;
            }
            return request;
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public Scheduler markDown(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            String key = getKeyHex(request);
            jedis.sadd(getDoneSetKey(task), key);
            return this;
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public Scheduler clear(Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.del(getWaitSetKey(task));
            jedis.del(getDoneSetKey(task));
            return this;
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public long getWaitRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.scard(getWaitSetKey(task));
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public long getDoneRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.scard(getDoneSetKey(task));
        } finally {
            pool.returnResource(jedis);
        }
    }
}
