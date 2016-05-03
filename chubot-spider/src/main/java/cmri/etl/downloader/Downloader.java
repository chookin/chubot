package cmri.etl.downloader;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.io.FileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Get document from internet.
 * <p/>
 * Created by zhuyin on 3/2/15.
 */
public interface Downloader extends Serializable {
    /**
     * Use the spider to download the request page.
     * @return result of download
     * @throws IOException
     */
    ResultItems download(Request request, Spider spider) throws IOException;

    default Logger getLogger(){
        return Logger.getLogger(Downloader.class);
    }

    default ResultItems useCache(Request request, Spider spider) throws IOException {
        ResultItems resultItems = new ResultItems(request, spider);
        Cache cache = new Cache(request, spider);
        if (cache.usable()) {
            switch (request.getTarget()) {
                case ByteArray:
                    return resultItems.setResource(FileUtils.readFileToByteArray(new File(cache.getFileName()))).cacheUsed(true);
                case Doc:
                    Document doc = Jsoup.parse(new File(cache.getFileName()), "utf-8", request.getUrl());
                    return resultItems.setResource(doc).cacheUsed(true);
                case File:
                    return resultItems.cacheUsed(true);
                case Json:
                    String body = FileHelper.readString(cache.getFileName());
                    return resultItems.setResource(body).cacheUsed(true);
            }
        }
        return null;
    }

    /**
     * Download the file of this url.
     * @param url the file's url.
     * @return the file path.
     * @throws IOException
     */
    default String download(String url) throws IOException {
        Request request = new Request().setUrl(url).setTarget(Request.TargetResource.File);
        Spider spider = new SpiderAdapter();
        download(request, spider);
        return request.getFilePath();
    }
}
