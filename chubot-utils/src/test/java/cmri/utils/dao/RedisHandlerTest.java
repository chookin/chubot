package cmri.utils.dao;

import cmri.utils.lang.RandomHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by zhuyin on 11/5/15.
 */
public class RedisHandlerTest {

    @Test
    public void testSet() throws Exception {
        String key = "test_" + UUID.randomUUID().toString();
        int code = RandomHelper.rand(1111,5555);
        RedisHandler.instance().set(key, code, 300);
        int ret = RedisHandler.instance().getObject(key);
        Assert.assertEquals(code, ret);
    }
}