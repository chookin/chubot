package cmri.etl.spider.monitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Created by zhuyin on 5/24/15.
 */
public interface SpiderMetricMBean extends Serializable{
    String getUUID();

    String getName();

    Date getStartTime();

    Date getStopTime();

    String getStatus();

    int getSleepMilliseconds();

    void setSleepMilliseconds(int millis);

    /**
     * @return thread count which is running
     */
    int getThread();

    long getDoneRequestsCount();

    long getWaitRequestsCount();

    long getSuccessPageCount();

    long getFailedPageCount();

    Collection<String> getFailedUrls();

    long getPagePerSecond();
}