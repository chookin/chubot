package cmri.etl.downloader;

import cmri.utils.web.NetworkHelper;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.spider.SpiderAdapter;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created by zhuyin on 3/8/15.
 */
public class TestSeleniumDownloader extends TestCase {
    @Override
    protected void setUp() {
        NetworkHelper.setDefaultProxy();
    }

    public void testRedirect() {
        String url = "http://a.m.taobao.com/i36381721825.htm?sid=394cc93586c21213&abtest=13&rn=8ab4fc8947a137b0829e59330ec4e4c0";
        try {
            ResultItems page = SeleniumDownloader.instance.download(new Request().setUrl(url),
                    new SpiderAdapter()
            );
            System.out.println(page.getResource());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
