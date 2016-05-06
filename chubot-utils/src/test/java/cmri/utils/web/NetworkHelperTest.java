package cmri.utils.web;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by zhuyin on 3/5/15.
 */
public class NetworkHelperTest {
    @Before
    public void setUp() {
    }

    @Test
    public void getIPs(){
        Collection<String> ips = NetworkHelper.getIPs();
        ips.forEach(System.out::println);
    }

    @Test
    public void getHostname(){
        String hostname = NetworkHelper.getHostname();
        System.out.println(hostname);
    }
}
