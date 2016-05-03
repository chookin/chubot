package cmri.etl.downloader;

import cmri.utils.web.NetworkHelper;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.utils.web.UrlHelper;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.io.FileHelper;
import cmri.utils.lang.StringHelper;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by zhuyin on 3/9/15.
 */
public class JsoupDownloaderTest {
    @Before
    public void setUp() {
        NetworkHelper.setDefaultProxy();
    }

    @Test
    @Ignore
    public void testDownloadBinary() {
        String url = "http://wd.jb51.net:81/201208/books/Hadoopqwzn_cn2_jb51.rar";
        try {
            byte[] doc = (byte[]) JsoupDownloader.getInstance().download(new Request().setUrl(url).setTarget(Request.TargetResource.ByteArray),new SpiderAdapter()
            ).getResource();
            FileHelper.save(doc, UrlHelper.getFilePath(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutCookie() throws IOException {
        String url = "http://s.plcloud.music.qq.com/fcgi-bin/fcg_get_diss_by_tag.fcg?categoryId=17&sortId=2&sin=0&ein=19&format=jsonp&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=GB2312&outCharset=utf-8&notice=0&platform=yqq&jsonpCallback=MusicJsonCallback&needNewCode=0";

        String cookie = "RK=CSWyS2NdfZ; pgv_pvi=4184944640; uid=25493040; pt2gguin=o0469308668; ptisp=cm; ptcz=0802eaad41f9d8893e3ee158457764b1c187a0447079210aa1e80db84c759b10; isVideo_DC=0; piao_city=10; pgv_info=ssid=s6632480880; pgv_pvid=2620759212; o_cookie=469308668";

        String header = "Referer=http://y.qq.com/y/static/taoge/taoge_list.html";

        String json = Jsoup.connect(url)
                .header("Referer", "http://y.qq.com/y/static/taoge/taoge_list.html")
                .cookies(StringHelper.parseHttpRequestCookie(cookie))
                .ignoreContentType(true)
                .maxBodySize(40960000)
                .execute().body();
        System.out.println(json);

        Request request = new Request()
                .setUrl(url)
                .setTarget(Request.TargetResource.Json)
                .addHeader(StringHelper.parseHttpRequestHeader(header))
                .addCookie(StringHelper.parseHttpRequestCookie(cookie))
                .setValidPeriod(0L);
        ResultItems resultItems = JsoupDownloader.getInstance().download(request,new SpiderAdapter());
        Assert.assertEquals(true, json.equals(resultItems.getResource()));
    }
}
