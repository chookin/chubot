package cmri.etl.scheduler;

import cmri.etl.common.Request;
import cmri.etl.common.Task;
import com.google.common.primitives.UnsignedBytes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by zhuyin on 3/2/15.
 */
public class QueueScheduler implements MonitorableScheduler {
    private final BlockingQueue<Request> queue = new LinkedBlockingDeque<>();
    private final ConcurrentSkipListSet<byte[]> doneRequests = new ConcurrentSkipListSet<>(UnsignedBytes.lexicographicalComparator());

    @Override
    public Scheduler push(Request request, Task task) {
        if (doneRequests.contains(getKey(request))) {
            return this;
        }
        queue.add(request);
        return this;
    }

    @Override
    public Request poll(Task task) {
        return queue.poll();
    }

    @Override
    public Scheduler markDown(Request request, Task task) {
        doneRequests.add(getKey(request));
        return null;
    }

    @Override
    public Scheduler clear(Task task) {
        queue.clear();
        doneRequests.clear();
        return this;
    }

    @Override
    public long getWaitRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public long getDoneRequestsCount(Task task) {
        return doneRequests.size();
    }
}
