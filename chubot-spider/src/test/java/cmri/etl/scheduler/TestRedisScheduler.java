package cmri.etl.scheduler;

import cmri.etl.proxy.Proxy;
import cmri.etl.common.Request;
import cmri.etl.common.Task;
import junit.framework.TestCase;

import java.io.Serializable;

/**
 * Created by zhuyin on 3/13/15.
 */
public class TestRedisScheduler extends TestCase {
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
    private MonitorableScheduler scheduler;

    protected void setUp() {
        scheduler = new RedisScheduler();
        scheduler.clear(task);
    }

    public void test() {
        Entity entity = new Entity().setCode("win").setName("choo");
        Request request = new Request().setUrl("http://redis.io/").putExtra("entity", entity);
        scheduler.push(request, task);
        Request poll = scheduler.poll(task);
        System.out.println(poll);

        Entity entityNew = poll.getExtra("entity", Entity.class);
        assertEquals(entity, entityNew);
        assertEquals(0, scheduler.getWaitRequestsCount(task));
        assertEquals(0, scheduler.getDoneRequestsCount(task));
        scheduler.markDown(poll, task);

        assertEquals(0, scheduler.getWaitRequestsCount(task));
        assertEquals(1, scheduler.getDoneRequestsCount(task));
    }

    static class Entity implements Serializable {
        String name;
        String code;
        Proxy proxy = new Proxy().set("country", "cn");

        public String getName() {
            return name;
        }

        public Entity setName(String name) {
            this.name = name;
            return this;
        }

        public String getCode() {
            return code;
        }

        public Entity setCode(String code) {
            this.code = code;
            return this;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public void setProxy(Proxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "name='" + name + '\'' +
                    ", code='" + code + '\'' +
                    ", proxy=" + proxy +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entity)) return false;

            Entity entity = (Entity) o;

            if (code != null ? !code.equals(entity.code) : entity.code != null) return false;
            if (name != null ? !name.equals(entity.name) : entity.name != null) return false;
            if (proxy != null ? !proxy.equals(entity.proxy) : entity.proxy != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (code != null ? code.hashCode() : 0);
            result = 31 * result + (proxy != null ? proxy.hashCode() : 0);
            return result;
        }
    }
}
