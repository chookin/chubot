package cmri.etl.scheduler;

import cmri.etl.common.Request;
import cmri.etl.common.Task;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/12/15.
 * <p/>
 * Priority scheduler. Request with higher priority will be polled earlier.<br>
 */
public class PriorityScheduler implements MonitorableScheduler {
    // to iterate sorted map in reverse order.
    private final SortedMap<Integer, Queue<Request>> queueMap = new TreeMap<>(Collections.reverseOrder());
    private final Set<byte[]> doneRequests = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Scheduler push(Request request, Task task) {
        int priority = request.getPriority();
        lock.writeLock().lock();
        try {
            if (doneRequests.contains(getKey(request))) {
                return this;
            }
            Queue<Request> queue = queueMap.get(priority);
            if (queue == null) {
                queue = new LinkedBlockingDeque<>();
                queueMap.put(priority, queue);
            }
            queue.add(request);
        } finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    @Override
    public Request poll(Task task) {
        lock.writeLock().lock();
        try {
            for (Map.Entry<Integer, Queue<Request>> pair : queueMap.entrySet()) {
                if (pair.getValue().isEmpty()) {
                    continue;
                }
                return pair.getValue().poll();
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Scheduler markDown(Request request, Task task) {
        lock.writeLock().lock();
        try {
            doneRequests.add(getKey(request));
            return this;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Scheduler clear(Task task) {
        lock.writeLock().lock();
        try {
            queueMap.clear();
            doneRequests.clear();
            return this;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long getWaitRequestsCount(Task task) {
        lock.readLock().lock();
        try {
            long count = 0;
            for (Map.Entry<Integer, Queue<Request>> pair : queueMap.entrySet()) {
                count += pair.getValue().size();
            }
            return count;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public long getDoneRequestsCount(Task task) {
        return doneRequests.size();
    }
}
