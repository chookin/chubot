package cmri.etl.downloader;

import cmri.etl.common.Request;
import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;

import java.io.File;
import java.util.Date;

/**
 * Created by zhuyin on 3/2/15.
 */
public class Cache {
    private final Request request;
    private final Spider spider;

    public Cache(Request request, Spider spider) {
        this.request = request;
        this.spider = spider;
    }

    public Request getRequest() {
        return request;
    }

    public Spider getSpider() {
        return spider;
    }

    /**
     * Can use cached resource?
     * @return true if can use cache.
     */
    public boolean usable() {
        // 优先处理"spider.cache.forced".当其为true时,只要缓存文件存在,则就认为缓存可用
        if(ConfigManager.getBool("spider.cache.forced", false)){
            if( new File(this.getFileName()).exists()){
                return true;
            }
        }

        if(!request.isCacheReadable()){
            return false;
        }
        long validateMilliseconds = request.getValidPeriod();
        if (validateMilliseconds == 0L) {
            return false;
        }
        File file = new File(this.getFileName());
        if (file.exists()) {
            long time = file.lastModified();
            long now = new Date().getTime();
            if (now - time < validateMilliseconds) { // this file is still new
                return true;
            }
        }
        return false;
    }

    public String getFileName() {
        return getRequest().getFilePath();
    }
}
