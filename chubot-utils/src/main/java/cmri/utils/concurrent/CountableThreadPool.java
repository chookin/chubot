package cmri.utils.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuyin on 3/3/15.
 */
public class CountableThreadPool {
    private int threadNum;
    private final AtomicInteger threadAlive = new AtomicInteger();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition condition = reentrantLock.newCondition();
    private ExecutorService executorService;

    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }

    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * @return Count of active thread.
     */
    public int getThreadAlive() {
        return threadAlive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void execute(final Runnable runnable) {
        if (threadAlive.get() >= threadNum) {
            try {
                reentrantLock.lock();
                while (threadAlive.get() >= threadNum) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();

        executorService.execute(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                throw t;
            } finally {
                try {
                    reentrantLock.lock();
                    threadAlive.decrementAndGet();
                    condition.signal();
                } finally {
                    reentrantLock.unlock();
                }
            }
        });
    }

    /**
     * @return {@code true} if this executor has been shut down
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
