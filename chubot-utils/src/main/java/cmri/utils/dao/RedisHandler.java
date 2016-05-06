package cmri.utils.dao;

import cmri.utils.lang.SerializationHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;

/**
 * Created by zhuyin on 11/2/15.
 */
public class RedisHandler {
    private final JedisPool pool;
    protected RedisHandler(){
        pool = JedisFactory.getJedisPool();
    }
    private static RedisHandler instance = new RedisHandler();
    public static RedisHandler instance(){
        return instance;
    }

    /**
     * Add the specified member to the set value stored at key. If member is already a member of the
     * set no operation is performed. If key does not exist a new set with the specified member as
     * sole member is created. If the key exists but does not hold a set value an error is returned.
     * the server.
     * @param key
     * @param value
     * @param expireSeconds a timeout on the specified key. After the timeout the key will be automatically deleted by
     *         already a member of the set
     */
    public void set(String key, String value, int expireSeconds){
        Jedis jedis = pool.getResource();
        try {
            jedis.set(key, value);
            jedis.expire(key, expireSeconds);
        }finally {
            pool.returnResource(jedis);
        }
    }

    public void set(String key, Serializable value, int expireSeconds){
        set(key, SerializationHelper.serialize(value), expireSeconds);
    }

    public String get(String key){
        Jedis jedis = pool.getResource();
        try {
            return jedis.get(key);
        }finally {
            pool.returnResource(jedis);
        }
    }

    public <T> T getObject(String key){
        Jedis jedis = pool.getResource();
        try {
            String str = jedis.get(key);
            return SerializationHelper.deserialize(str);
        }finally {
            pool.returnResource(jedis);
        }
    }
    /**
     * Remove the specified key. If a given key does not exist no operation is performed for this key.
     * @param key the key need remove
     * @return Integer reply, specifically: 1 if the key was removed
     *         0 if the key was not existed
     */
    public Long del(String key){
        Jedis jedis = pool.getResource();
        try {
            return jedis.del(key);
        }finally {
            pool.returnResource(jedis);
        }
    }

}
