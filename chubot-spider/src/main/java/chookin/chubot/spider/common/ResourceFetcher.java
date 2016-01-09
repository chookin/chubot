package chookin.chubot.spider.common;

import cmri.etl.downloader.Downloader;
import cmri.etl.downloader.HttpClientDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by zhuyin on 10/16/15.
 */
public class ResourceFetcher {
    public Object fetch(Element e, ResourceMeta meta){
        switch (meta.type){
            case FILE:
                String url = e.select(meta.path).first().absUrl("href");
                HttpClientDownloader.getInstance().download(url);
        }
    }
}
