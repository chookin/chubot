package cmri.etl.downloader;

import cmri.utils.web.NetworkHelper;
import cmri.etl.common.Request;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.configuration.ConfigManager;
import junit.framework.TestCase;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by zhuyin on 3/8/15.
 */
public class TestCasperJsDownloader extends TestCase {
    String webUserAgent;

    @Override
    protected void setUp() {
        NetworkHelper.setDefaultProxy();
        webUserAgent = ConfigManager.get("spider.web.userAgent");
    }

    public void testRedirect() {
        String url = "http://a.m.taobao.com/i36381721825.htm?sid=394cc93586c21213&abtest=13&rn=8ab4fc8947a137b0829e59330ec4e4c0";
        try {
            Document doc = (Document) CasperJsDownloader.getInstance().download(new Request().setUrl(url).setUserAgent(webUserAgent),
                    new SpiderAdapter()
            ).getResource();
            System.out.println(doc.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
