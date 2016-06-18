package cmri.etl.spider.monitor;

import cmri.etl.scheduler.MonitorableScheduler;
import cmri.etl.spider.Spider;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Date;

/**
 * Created by zhuyin on 5/24/15.
 */
public class SpiderMetric implements SpiderMetricMBean {
    protected static Logger logger = Logger.getLogger(SpiderMetric.class);
    protected final Spider spider;
    protected final MonitorSpiderListener monitorSpiderListener;

    public SpiderMetric(MonitorSpiderListener monitorSpiderListener) {
        this.monitorSpiderListener = monitorSpiderListener;
        this.spider = monitorSpiderListener.getSpider();
    }

    @Override
    public String getUUID(){
        return spider.uuid();
    }

    @Override
    public String getName() {
        return spider.name();
    }

    @Override
    public long getWaitRequestsCount() {
        if (spider.scheduler() instanceof MonitorableScheduler) {
            return ((MonitorableScheduler) spider.scheduler()).getWaitRequestsCount(spider);
        }
        logger.warn("Get waitRequestsCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!");
        return -1;
    }
    @Override
    public long getDoneRequestsCount() {
        if (spider.scheduler() instanceof MonitorableScheduler) {
            return ((MonitorableScheduler) spider.scheduler()).getDoneRequestsCount(spider);
        }
        logger.warn("Get doneRequestsCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!");
        return -1;
    }

    @Override
    public long getSuccessPageCount() {
        return monitorSpiderListener.getSuccessCount().get();
    }

    @Override
    public long getFailedPageCount() {
        return monitorSpiderListener.getFailedCount().get();
    }

    @Override
    public Collection<String> getFailedUrls() {
        return monitorSpiderListener.getFailedUrls();
    }

    @Override
    public String getStatus() {
        return spider.getStatus().name();
    }

    @Override
    public int getSleepMilliseconds() {
        return spider.getSleepMillis();
    }

    @Override
    public void setSleepMilliseconds(int millis) {
        spider.setSleepMillis(millis);
    }

    @Override
    public int getThread() {
        return spider.getThreadAlive();
    }

    @Override
    public Date getStartTime() {
        return monitorSpiderListener.getStartTime();
    }

    @Override
    public Date getStopTime(){
        return monitorSpiderListener.getStopTime();
    }

    @Override
    public long getPagePerSecond() {
        int runSeconds = (int) (System.currentTimeMillis() - getStartTime().getTime()) / 1000;
        return getSuccessPageCount() / runSeconds;
    }

}