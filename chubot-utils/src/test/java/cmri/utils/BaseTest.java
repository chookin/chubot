package cmri.utils;

import cmri.utils.web.NetworkHelper;
import org.junit.Before;

/**
 * Created by chookin on 16/3/28.
 */
public class BaseTest {
    @Before
    protected void setUp() {
        NetworkHelper.setDefaultProxy();
    }
}
