package cmri.etl.spider.monitor;

import cmri.etl.common.Request;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderListener;
import cmri.utils.dao.MongoHandler;
import cmri.utils.lang.JsonHelper;
import cmri.utils.lang.SerializationHelper;
import com.mongodb.BasicDBObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhuyin on 5/24/15.
 */
public class MonitorSpiderListener implements SpiderListener {
    private final Spider spider;
    /**
     * start time of the monitored spider
     */
    private Date startTime;
    private Date stopTime;

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failedCount = new AtomicLong(0);
    private final Set<String> failedUrls = new CopyOnWriteArraySet<>();

    public MonitorSpiderListener(Spider spider){
        this.spider = spider;
        this.spider.addListener(this);
    }

    public Spider getSpider(){
        return spider;
    }

    @Override
    public void onStart(Spider spider) {
        startTime = new Date();
    }

    @Override
    public void onStop(Spider spider) {
        stopTime = new Date();
        getLogger().info("Statistics: success " + getSuccessCount() + " pages, and failed " + getFailedCount() + " pages");
    }

    @Override
    public void onSuccess(Spider spider, Request request) {
        successCount.incrementAndGet();
    }

    @Override
    public void onError(Spider spider, Request request) {
        if(spider.retry(request)){
            return;
        }
        failedUrls.add(request.getUrl());
        failedCount.incrementAndGet();
        dumpFailedRequest(request);
    }

    public Date getStartTime(){
        return startTime;
    }

    public Date getStopTime(){
        return stopTime;
    }
    public AtomicLong getSuccessCount() {
        return successCount;
    }

    public AtomicLong getFailedCount() {
        return failedCount;
    }

    public Set<String> getFailedUrls() {
        return failedUrls;
    }

    /**
     * 必须存储{@link Request},因为一个web资源请求不仅包括url,还包括cookie等数据
     * @param request 需要存储的请求
     */
    private void dumpFailedRequest(Request request) {
        MongoHandler dao = MongoHandler.instance();
        try{
            dao.updateOrInsert("requestFailed", getBasicDBObject(request));
        }finally {
            dao.close();
        }
    }

    private String getId(Request entity) {
        return DigestUtils.md5Hex(SerializationHelper.serialize(entity));
    }

    private BasicDBObject getBasicDBObject(Request entity) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", getId(entity));
        doc.put("url", entity.getUrl());
        doc.put("downloader", entity.getDownloader().getClass().getName());
        doc.put("pageProcessor", entity.getPageProcessor().getClass().getName());
        doc.put("priority", entity.getPriority());
        doc.put("retryCount", entity.getRetryCount());
        doc.put("properties", JsonHelper.toJson(entity.getExtra()));
        doc.put("time", new Date());
        return doc;
    }
}
