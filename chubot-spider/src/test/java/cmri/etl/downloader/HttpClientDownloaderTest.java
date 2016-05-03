package cmri.etl.downloader;

import cmri.utils.web.NetworkHelper;
import cmri.etl.common.Request;
import cmri.etl.spider.SpiderAdapter;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by zhuyin on 7/5/15.
 */
public class HttpClientDownloaderTest {
    @Before
    public void setUp() {
        NetworkHelper.setDefaultProxy();
    }

    @Test
    public void testDownload() throws Exception {
        String url = "http://www.126.com";
        Request request = new Request().setUrl(url)
                .setTarget(Request.TargetResource.Doc)
                .setDownloader(HttpClientDownloader.getInstance())
                .setValidPeriod(0L);
        new SpiderAdapter().addRequest(request)
                .run();
    }
}