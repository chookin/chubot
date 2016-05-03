package cmri.etl.scheduler;

import cmri.etl.common.Request;
import cmri.etl.common.Task;
import junit.framework.TestCase;
import redis.clients.jedis.JedisPool;

/**
 * Created by zhuyin on 3/13/15.
 */
public class TestRedisPriorityScheduler extends TestCase {
    private MonitorableScheduler scheduler;

    protected void setUp() {
        scheduler = new RedisPriorityScheduler(new JedisPool("localhost", 6379));
    }

    public void test() {
        Task task = new Task() {
            @Override
            public String uuid() {
                return "1";
            }

            @Override
            public String name() {
                return "test";
            }
        };
        Request request = new Request().setUrl("http://redis.io/").putExtra("r", "1").setPriority(1);
        Request request1 = new Request().setUrl("http://www.aol.com/").putExtra("a", "2").setPriority(9);

        scheduler.clear(task);
        scheduler.push(request, task).push(request1, task);
        Request poll = scheduler.poll(task);
        System.out.println(poll);
        assertEquals(request1, poll);
        assertEquals(1, scheduler.getWaitRequestsCount(task));
        assertEquals(0, scheduler.getDoneRequestsCount(task));
        scheduler.markDown(poll, task);

        poll = scheduler.poll(task);
        System.out.println(poll);
        assertEquals(0, scheduler.getWaitRequestsCount(task));
        assertEquals(1, scheduler.getDoneRequestsCount(task));
    }
}
