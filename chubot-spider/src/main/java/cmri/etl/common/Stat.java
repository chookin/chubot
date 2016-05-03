package cmri.etl.common;

import cmri.utils.lang.Pair;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 状态统计
 * <p>
 * Created by zhuyin on 7/6/15.
 */
public class Stat implements Serializable {
    /**
     * Only stat recent several times.
     */
    private int queueCapacity = 3;
    /**
     * Only stat recent.
     */
    private int expireMillis = 180000;
    /**
     * If fail count is bigger than this value, then consider stat is not ok.
     */
    private int failCountThreshold = 2;
    private final Queue<Pair<Long, Boolean>> stats = new ConcurrentLinkedQueue<>();

    public Stat(int queueCapacity, int expireMillis, int failCountThreshold) {
        this.queueCapacity = queueCapacity;
        this.expireMillis = expireMillis;
        this.failCountThreshold = failCountThreshold;
    }

    /**
     * 新增状态
     *
     * @param ok 状态
     * @return this
     */
    public Stat add(Boolean ok) {
        stats.offer(new Pair<>(System.currentTimeMillis(), ok));
        if (stats.size() > queueCapacity) {
            stats.poll();
        }
        return this;
    }

    /**
     * 判断状态.若在最近一段时间、最近若干次没有成功，这意味着不可用。
     *
     * @return this
     */
    public boolean isOK() {
        removeExpires();
        if (stats.isEmpty()) {
            return true;
        }
        if (getFailCount() > failCountThreshold) {
            return false;
        }
        return true;
    }

    /**
     * 移除过期的状态
     *
     * @return this
     */
    public Stat removeExpires() {
        while (!stats.isEmpty()) {
            Pair<Long, Boolean> front = stats.peek();
            if (System.currentTimeMillis() - front.getKey() > expireMillis) {
                stats.poll();
            } else {
                break;
            }
        }
        return this;
    }

    /**
     * 获取失败计数
     *
     * @return 失败计数
     */
    public int getFailCount() {
        int cnt = 0;
        for (Pair<Long, Boolean> item : stats) {
            if (!item.getValue()) {
                ++cnt;
            }
        }
        return cnt;
    }
}
