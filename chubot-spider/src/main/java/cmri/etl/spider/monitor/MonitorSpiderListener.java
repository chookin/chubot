package cmri.etl.spider.monitor;

import cmri.etl.common.Request;
import cmri.etl.dao.RequestDAO;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderListener;

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

    public MonitorSpiderListener(Spider spider) {
        this.spider = spider;
        this.spider.addListener(this);
    }

    public Spider getSpider() {
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
        if (spider.retry(request)) {
            return;
        }
        failedUrls.add(request.getUrl());
        failedCount.incrementAndGet();
        RequestDAO.getInstance().saveFailed(request);
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getStopTime() {
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
}
