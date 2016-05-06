package cmri.utils.web;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by zhuyin on 3/5/15.
 */
public class UrlHelperTest extends TestCase {
    private String simpleUrl;

    @Override
    protected void setUp() {
        simpleUrl = "http://www.126.com";
    }

    public void testEraseProtocol() {
        String dst = UrlHelper.eraseProtocol(simpleUrl);
        assertEquals("www.126.com", dst);
    }

    public void testEraseProtocolAndStart3W() {
        String dst = UrlHelper.eraseProtocolAndStart3W(simpleUrl);
        assertEquals("126.com", dst);
    }

    public void testGetProtocol() {
        assertEquals("http", UrlHelper.getProtocol(simpleUrl));
    }

    public void testGetBaseDomain() {
        String url = "http://a.m.taobao.com/items/i42471538944.htm?sid=60338c63d854b9ef&abtest=4&rn=2bdac199debd8eb86b7234fc1d3c712e";
        assertEquals("a.m.taobao.com", UrlHelper.getBaseDomain(url));
    }
    public void testGetPath(){
        String url = "http://snapshot:8080/materials/homepages?sig=0ae7ea9680aeedfb77c2069fd14bd1fe&time=1450193196206";
        String path = UrlHelper.getPath(url);
        Assert.assertEquals("/materials/homepages", path);
    }
    public void testGetFileName() {
        String url = "http://a.m.taobao.com/items/i42471538944.htm?sid=60338c63d854b9ef&abtest=4&rn=2bdac199debd8eb86b7234fc1d3c712e";
        String fileName = UrlHelper.getFilePath(url);
        System.out.println(fileName);
        assertEquals(true, fileName.endsWith("a.m.taobao.com/items/1dee39a9c6bb4e2e24684315f4d8bdf5"));

        url = "http://item.jd.com/20012417.html";
        fileName = UrlHelper.getFilePath(url);
        assertEquals(true, fileName.endsWith("item.jd.com/e08f561d502ceb41145c7a82d5d52540"));
        url = simpleUrl;
        fileName = UrlHelper.getFilePath(url);
        assertEquals(true, fileName.endsWith("126.com/f85746593919918d677b14ada3197db2"));
    }
}
