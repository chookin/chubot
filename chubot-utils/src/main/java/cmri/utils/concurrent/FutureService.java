package cmri.utils.concurrent;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zhuyin on 12/26/14.
 */
public class FutureService<T> {
    private static final Logger LOG = Logger.getLogger(FutureService.class);
    private ExecutorService executorService;
    private final int poolSize;
    private final Collection<T> items;
    private final CallableCreator<T> creator;

    public FutureService(Collection<T> items, CallableCreator<T> creator, int poolSize) {
        this.items = items;
        this.creator = creator;
        this.poolSize = poolSize;
    }

    /**
     * create futures and get result, doing sync.
     * @return acc of future results.
     */
    public long action() {
        List<Future<Long>> futures = createFutures(items, creator);
        return getFutureResult(futures);
    }

    List<Future<Long>> createFutures(Collection<T> items, CallableCreator<T> creator) {
        ArrayList<T>[] batchEntity = shuffle(items);
        executorService = Executors.newFixedThreadPool(poolSize);
        List<Future<Long>> futures = new ArrayList<>();
        for (Collection<T> batch : batchEntity) {
            futures.add(executorService
                    .submit(creator.create(batch)));
        }
        return futures;
    }

    /**
     * 把items分成poolSize份（类似于洗牌，每个玩家依次轮流发到一张牌，直到牌发完）
     * @return 分组结果
     */
    ArrayList<T>[] shuffle(Collection<T> items) {
        ArrayList<T>[] batchEntity = new ArrayList[this.poolSize];
        for (int i = 0; i < batchEntity.length; ++i) {
            batchEntity[i] = new ArrayList<>();
        }
        int index = 0;
        for (T entity : items) {
            batchEntity[index % poolSize].add(entity);
            ++index;
        }
        return batchEntity;
    }

    /**
     * 等待所有任务执行完成，并返回future结果汇总
     * @return 获得future执行结果
     */
    long getFutureResult(Collection<Future<Long>> futures) {
        while (!Thread.currentThread().isInterrupted()) {
            boolean allDone = true;
            for (Future<Long> future : futures) {
                if (!future.isDone()) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                break;
            } else {
                ThreadHelper.sleep(1000);
            }
        }

        long count = 0;
        for (Future<Long> future : futures) {
            try {
                if(!future.isCancelled())
                    count += future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error(future, e);
            }
        }
        this.executorService.shutdown();
        return count;
    }

    public interface CallableCreator<T> {
        Callable<Long> create(Collection<T> items);
    }
}
