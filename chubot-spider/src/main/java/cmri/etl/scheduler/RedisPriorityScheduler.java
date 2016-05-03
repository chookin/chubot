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
public class RedisPriorityScheduler implements MonitorableScheduler {
    private final JedisPool pool;

    public RedisPriorityScheduler(){
        this(JedisFactory.getJedisPool());
    }

    public RedisPriorityScheduler(JedisPool pool) {
        this.pool = pool;
    }

    private String getWaitSetKey(Task task, int priority) {
        return "pw-" + task.name() + "-" + priority;
    }
    // If a spider use this scheduler quit with undone requests, then these undone requests are always in process. So, need to set timeout for request on the state of in process. If timeout, then back to wait state. But, it's not better, for request failed and push again...
    // private String getInProcessQueueKey(Task task){return task.name();}

    private String getDoneSetKey(Task task) {
        return "pd-" + task.name();
    }

    private long getSetCount(Task task, int priority, Jedis jedis) {
        return jedis.scard(getWaitSetKey(task, priority));
    }

    @Override
    public Scheduler push(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            String key = getKeyHex(request);
            // 检查该request是否已完成,即查看完成队列中是否有该request的记录
            if (jedis.sismember(getDoneSetKey(task), key)) {
                return this;
            }
            jedis.sadd(getWaitSetKey(task, request.getPriority()), SerializationHelper.serialize(request));
        } finally {
            pool.returnResource(jedis);
        }
        return this;
    }

    @Override
    public Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            // retrieve from priority 9, if not found valid request, then retrieve the lower queue.
            for (int priority = 9; priority >= 0; --priority) {
                if (getSetCount(task, priority, jedis) == 0) {
                    continue;
                }
                String str = jedis.spop(getWaitSetKey(task, priority));
                if(str == null){ // maybe polled just before by other agent.
                    continue;
                }
                Request request = SerializationHelper.deserialize(str);
                if (request == null) {
                    return null;
                }
                return request;
            }
            return null;
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
            String[] setKeys = new String[10];
            for (int priority = 9; priority >= 0; --priority) {
                setKeys[priority] = getWaitSetKey(task, priority);
            }
            jedis.del(setKeys);
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
            long count = 0;
            for (int priority = 9; priority >= 0; --priority) {
                count += getSetCount(task, priority, jedis);
            }
            return count;
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public long getDoneRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.scard(getDoneSetKey(task)).intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }
}
