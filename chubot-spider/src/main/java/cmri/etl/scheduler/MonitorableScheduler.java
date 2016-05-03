package cmri.etl.scheduler;

import cmri.etl.common.Task;

/**
 * Created by zhuyin on 5/24/15.
 */
public interface MonitorableScheduler extends Scheduler {

    /**
     * @return 该任务还有多少条请求没有被处理
     */
    long getWaitRequestsCount(Task task);

    /**
     * @return 该任务已处理了多少条请求
     */
    long getDoneRequestsCount(Task task);
}
