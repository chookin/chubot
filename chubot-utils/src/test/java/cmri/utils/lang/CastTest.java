package cmri.utils.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhuyin on 10/30/15.
 */
public class CastTest {
    @Test
    public void castNull(){
        Object o = null;
        String str = (String) o;
        Assert.assertEquals(null, str);

        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) o;
        Assert.assertEquals(null, pair);
    }
}
