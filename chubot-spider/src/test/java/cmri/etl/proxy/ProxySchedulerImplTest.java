package cmri.etl.proxy;

import cmri.utils.web.NetworkHelper;
import cmri.etl.common.Request;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by zhuyin on 7/22/15.
 */
public class ProxySchedulerImplTest {
    @Before
    public void setUp() {
        NetworkHelper.setDefaultProxy();
    }

    @Test
    public void testGetProxy() throws Exception {
        ProxyScheduler scheduler = new ProxySchedulerImpl();
        Proxy proxy = scheduler.getProxy(new Request("http://www.126.com", null));
        System.out.println(proxy);
    }
}