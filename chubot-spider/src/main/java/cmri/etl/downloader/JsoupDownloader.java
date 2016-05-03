package cmri.etl.downloader;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhuyin on 3/2/15.
 */
public class JsoupDownloader implements Downloader {
    static final JsoupDownloader instance = new JsoupDownloader();

    public static JsoupDownloader getInstance() {
        return instance;
    }

    protected JsoupDownloader() {
    }

    public Document getDocument(String url) throws IOException {
        return (Document) download(
                new Request().setUrl(url),
                new SpiderAdapter()
        ).getResource();
    }

    public Document getDocument(String url, String userAgent) throws IOException {
        return (Document) download(
                new Request().setUrl(url).setUserAgent(userAgent),
                new SpiderAdapter()
        ).getResource();
    }

    @Override
    public ResultItems download(Request request, Spider spider) throws IOException {
        String url = request.getUrl();
        getLogger().trace("get " + url);

        ResultItems resultItems = useCache(request, spider);
        if (resultItems != null) {
            return resultItems;
        }
        resultItems = new ResultItems(request, spider);

        switch (request.getTarget()) {
            case ByteArray:
                return resultItems.setResource(new StreamDownloader().downloadByteArray(request, spider));
            case Doc:
                return resultItems.setResource(getResponse(request).parse());
            case Json:
                return resultItems.setResource(getResponse(request).body());
            case File:
                new StreamDownloader().downloadFile(request, spider);
                return resultItems.setResource("");
        }
        return resultItems;
    }

    Connection.Response getResponse(Request request) throws IOException {
        Connection conn = Jsoup.connect(request.getUrl())
                .ignoreContentType(true)
                .maxBodySize(40960000)
                .timeout(request.getTimeout())
                .userAgent(request.getUserAgent())
                .data(request.getHeaders())
                .cookies(request.getCookies());
        updateHeader(conn, request);
        updateMethod(conn, request);
        return conn.execute();
    }

    private void updateHeader(Connection conn, Request request) {
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            conn.header(entry.getKey(), entry.getValue());
        }
    }

    private void updateMethod(Connection conn, Request request) {
        switch (request.getMethod()) {
            case GET:
                conn.method(Connection.Method.GET);
                break;
            case POST:
                conn.method(Connection.Method.POST);
                break;
            default:
                throw new RuntimeException("unknown method: "+ request.getMethod());
        }
    }
}
