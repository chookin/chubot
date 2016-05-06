package cmri.utils.configuration;

import org.junit.Test;

/**
 * Created by zhuyin on 6/30/15.
 */
public class ConfigManagerTest {

    @Test
    public void testGetProperty() throws Exception {
        String port = ConfigManager.get("mongo.port");
        String user = ConfigManager.get("mongo.user");
        System.out.println("user: " + user);
    }
}