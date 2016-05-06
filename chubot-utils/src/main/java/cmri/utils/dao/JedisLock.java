package cmri.utils.dao;

/**
 * Created by zhuyin on 5/19/15.
 */

import redis.clients.jedis.Jedis;

import java.util.Random;

public class JedisLock {
    /** 加锁标志 */
    public static final String LOCKED = "TRUE";
    /** 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒 */
    public static final long MILLI_NANO_CONVERSION = 1000 * 1000L;
    /** 默认超时时间（毫秒） */
    public static final long DEFAULT_TIME_OUT = 1000 * 300;
    /** 默认锁的超时时间（秒），过期删除 */
    public static final int DEFAULT_LOCK_EXPIRE = 3 * 60;
    public static final Random RANDOM = new Random();
    private final Jedis jedis;
    private final String key;
    // 锁状态标志
    private boolean locked = false;

    /**
     * This creates a RedisLock
     */
    public JedisLock(Jedis jedis, String key) {
        this.key = "lock_" + key;
        this.jedis = jedis;
    }

    /**
     * 加锁
     * 应该以：
     * lock();
     * try {
     * 		doSomething();
     * } finally {
     * 		unlock()；
     * }
     * 的方式调用
     * @param timeout 超时时间
     * @param expire 锁的超时时间（秒），过期删除
     * @return 成功或失败标志
     */
    public boolean lock(long timeout, int expire) {
        long nano = System.nanoTime();
        timeout *= MILLI_NANO_CONVERSION;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (this.jedis.setnx(this.key, LOCKED) == 1) {
                    this.jedis.expire(this.key, expire);
                    this.locked = true;
                    return true;
                }
                Thread.sleep(3, RANDOM.nextInt(500));
            }
        } catch (Exception e) {
            throw new RuntimeException("Redis locking error for key "+this.key, e);
        }
        return false;
    }

    /**
     * 加锁
     * 应该以：
     * lock();
     * try {
     * 		doSomething();
     * } finally {
     * 		unlock()；
     * }
     * 的方式调用
     * @return 成功或失败标志
     */
    public boolean lock() {
        return lock(DEFAULT_TIME_OUT, DEFAULT_LOCK_EXPIRE);
    }

    /**
     * 解锁
     */
    public void unlock() {
        if (this.locked) {
            this.jedis.del(this.key);
        }
    }
}

