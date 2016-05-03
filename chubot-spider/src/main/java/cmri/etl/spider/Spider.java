package cmri.etl.spider;

import cmri.etl.common.Request;
import cmri.etl.common.Task;
import cmri.etl.pipeline.Pipeline;
import cmri.etl.proxy.Proxy;
import cmri.etl.scheduler.Scheduler;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zhuyin on 3/2/15.
 */
public interface Spider extends Runnable, Task {
    Spider init(Map<String, String> options);

    Scheduler scheduler();

    Spider addListener(SpiderListener... listeners);

    Spider addPipeline(Pipeline... pipelines);

    Spider setProxy(Proxy proxy);

    Proxy getProxy(Request request);

    int getSleepMillis();

    Spider setSleepMillis(int sleepMillisecond) ;

    /**
     * start with more than one threads
     *
     * @param threadNum the new thread num.
     * @return this
     */
    Spider setThreadNum(int threadNum);

    Spider addRequest(Collection<Request> requests);
    /**
     * If the request retry count is already bigger than 6, then no retry again, and is marked failed. Or else, increase the retry count, and push the request to scheduler.
     * @param request if null, ignore it.
     */
    Spider addRequest(Request request);

    /**
     * Whether add the request to queue, judge by request retry count.
     * @return true if need retry.
     */
    boolean retry(Request request);

    /**
     * 异步方式启动spider
     */
    void start();

    /**
     * 异步方式stop spider
     */
    void stop();

    /**
     * @return count of running threads
     */
    int getThreadAlive();

    /**
     * @return running status
     * @see Status
     */
    Status getStatus();

    /**
     * 针对request请求失败的情况，做重新添加请求到队列、计量请求失败等处理
     * @param request 失败的请求
     */
    void onError(Request request);

    /**
     * Process specific urls without url discovering.
     * @param requests requests to process
     */
    Spider test(Request... requests) throws Exception;

    /**
     * Process specific urls without url discovering.
     * @param requests requests to process
     */
    Spider test(Collection<Request> requests) throws Exception;

    enum Status {
        New,
        Inited,
        Running,
        Stop,
        Stopped
    }
}
