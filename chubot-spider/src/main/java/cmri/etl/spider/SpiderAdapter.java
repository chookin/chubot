package cmri.etl.spider;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.validator.PageValidateHelper;
import cmri.etl.pipeline.ConsolePipeline;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.pipeline.Pipeline;
import cmri.etl.processor.PageProcessor;
import cmri.etl.proxy.Proxy;
import cmri.etl.proxy.ProxyScheduler;
import cmri.etl.proxy.ProxySchedulerImpl;
import cmri.etl.scheduler.Scheduler;
import cmri.etl.spider.monitor.SpiderMonitor;
import cmri.etl.validator.PageDeniedException;
import cmri.utils.concurrent.CountableThreadPool;
import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.configuration.ConfigManager;
import org.apache.commons.lang3.Validate;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import javax.management.JMException;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhuyin on 3/2/15.
 */
public class SpiderAdapter implements Spider {
    private static final Logger LOG = Logger.getLogger(Spider.class);
    private final String uuid = UUID.randomUUID().toString();
    private final String name;
    private final Scheduler scheduler;
    private final Set<Pipeline> pipelines = new HashSet<>();
    /**
     * 用于把Spider的事件通知出去
     */
    private final Collection<SpiderListener> listeners = new CopyOnWriteArrayList<>();
    private CountableThreadPool threadPool;
    private Proxy proxy = null;
    private Status stat = Status.New;
    /**
     * 调度代理资源,用户从代理资源池中选取代理
     */
    private final ProxyScheduler proxyScheduler;

    /**
     * 抓取间隔,单位毫秒
     */
    private int sleepMillis;
    private int threadNum;

    /**
     * monitor the spider status
     */
    private final boolean statusMonitor;

    public SpiderAdapter() {
        this(null);
    }

    public SpiderAdapter(String name) {
        this(name, new HashMap<>());
    }

    public SpiderAdapter(String name, Map<String, String> paras) {
        this.name = generateTaskName(name);
        this.statusMonitor = ConfigManager.getBool("spider.status.monitor", paras, true);
        this.scheduler = getScheduler(paras);
        this.proxyScheduler = ProxySchedulerImpl.getInstance();
        init(paras);
    }

    Scheduler getScheduler(Map<String, String> paras) {
        String schedulerClass = ConfigManager.get("spider.scheduler", paras, "cmri.etl.scheduler.PriorityScheduler");
        try {
            return (Scheduler) Class.forName(schedulerClass).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SpiderAdapter init(Map<String, String> paras) {
        this.setSleepMillis(ConfigManager.getInt("spider.download.sleepMillis", paras, 5000));
        this.setThreadNum(ConfigManager.getInt("spider.download.concurrent.num", paras, 1));
        return this;
    }

    private String generateTaskName(String name) {
        String prefix = "c-"; // "collect-"
        if (name == null || name.isEmpty()) {
            return "anon"; // anonymous
        } else if (name.startsWith(prefix)) {
            return name;
        } else {
            return prefix + name;
        }
    }

    @Override
    public String uuid() {
        return uuid;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spider)) return false;

        Spider spider = (Spider) o;

        if (!uuid.equals(spider.uuid())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "Spider{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    public Scheduler scheduler() {
        return this.scheduler;
    }

    public Spider addListener(SpiderListener... listeners) {
        for (SpiderListener listener : listeners) {
            this.listeners.add(listener);
        }
        return this;
    }

    public Spider addPipeline(Pipeline... pipelines) {
        for (Pipeline pipeline : pipelines) {
            if (this.pipelines.contains(pipeline)) {
                continue;
            }
            this.pipelines.add(pipeline);
        }
        return this;
    }

    /**
     * 强制使用指定代理
     *
     * @param proxy 所强制指定的代理
     * @return this
     */
    public Spider setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * 获取用于指定请求的代理.
     */
    public Proxy getProxy(Request request) {
        if (this.proxy != null) {
            return proxy;
        }
        return proxyScheduler.getProxy(request);
    }

    public int getSleepMillis() {
        return sleepMillis;
    }

    public Spider setSleepMillis(int millis) {
        this.sleepMillis = millis;
        return this;
    }

    private void checkIfRunning() {
        if (stat == Status.Running) {
            throw new IllegalStateException("Spider is already running!");
        }
    }

    /**
     * start with more than one threads
     *
     * @param threadNum the new thread num.
     * @return this
     */
    public Spider setThreadNum(int threadNum) {
        Validate.isTrue(threadNum > 0, "threadNum should be more than zero!");
        checkIfRunning();
        this.threadNum = threadNum;
        return this;
    }

    /**
     * @return count of running threads
     */
    public int getThreadAlive() {
        if (threadPool == null) {
            return 0;
        }
        return threadPool.getThreadAlive();
    }

    /**
     * @return running status
     * @see Status
     */
    public Status getStatus() {
        return stat;
    }

    private void initComponent() {
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
            pipelines.add(new FilePipeline());
        }
        if (threadPool == null || threadPool.isShutdown()) {
            threadPool = new CountableThreadPool(threadNum);
        }
        stat = Status.Inited;
    }

    private Spider doRegisterToMonitor() {
        if (statusMonitor) {
            try {
                SpiderMonitor.instance().register(this);
            } catch (JMException e) {
                LOG.error("fail to register jmx of spider " + this.name(), e);
            }
        }
        return this;
    }

    /**
     * 同步方式启动spider抓取数据
     */
    @Override
    public void run() {
        try {
            onStart();
            while (!Thread.currentThread().isInterrupted() && stat == Status.Running) {
                Request request = scheduler.poll(this);
                if (request == null) {
                    if (threadPool.getThreadAlive() == 0) {
                        request = scheduler.poll(this); // must has these check, or else exits a bug: if after poll a null, the old request process just done and then submit a new request, but the new request will be miss processed.
                    } else { // has request processing.
                        ThreadHelper.sleep(10);
                        continue;
                    }
                }
                if (request == null) {
                    break;
                }
                if (threadNum == 1) {
                    syncProcess(request);
                } else {
                    asyncProcess(request);
                }
            }
        } catch (Throwable e) {
            LOG.error(null, e);
        } finally {
            onStop();
        }
    }

    /**
     * 同步处理
     *
     * @param request 待处理的请求
     */
    private void syncProcess(Request request) {
        ResultItems page = null;
        try {
            page = processRequest(request);
        } catch (MalformedURLException e) {
            logException(request, e);
        } catch (HttpStatusException e) {
            onError(request);
            logException(request, e.toString());
            switch (e.getStatusCode()) {
                case HttpURLConnection.HTTP_NOT_FOUND: // 如果遇到404等错误，则不会重新请求
                case HttpURLConnection.HTTP_INTERNAL_ERROR: // a problem when extract yhd, such as http://item.yhd.com/item/43583907 (货物已下架), no use even many retry.
                    break;
                default:
                    addRequest(request);
            }
        } catch (ConnectTimeoutException | UnknownHostException | SocketException | SocketTimeoutException | PageDeniedException e) {
            onError(request);
            logException(request, e.toString());
            addRequest(request);
        } catch (Exception e) {
            onError(request);
            logException(request, e);
            addRequest(request);
        }
        if (page != null) {
            if (page.isCacheUsed()) { // if not use cache, then no need to sleep
                return;
            }
        }
        ThreadHelper.sleep(sleepMillis);
    }

    private void logException(Request request, Exception e) {
        LOG.error("Failed to process " + request + ". ", e);
    }

    private void logException(Request request, String message) {
        LOG.error("Failed to process " + request + ". " + message);
    }

    /**
     * 从线程池中获取可用线程，做异步处理
     *
     * @param request 待处理的请求
     */
    private void asyncProcess(Request request) {
        threadPool.execute(() -> syncProcess(request));
    }

    /**
     * 异步方式启动spider
     */
    public void start() {
        Thread thread = new Thread(this);
        // 当所有的非守护线程结束时，程序也就终止了，同时会杀死进程中的所有守护线程。反过来说，只要任何非守护线程还在运行，程序就不会终止。
        thread.setDaemon(false);
        thread.start();
    }

    public void stop() {
        stat = Status.Stop;
        LOG.info("Start to stop spider " + uuid());
    }

    /**
     * Process specific urls without url discovering.
     *
     * @param requests requests to process
     */
    public Spider test(Request... requests) throws IOException {
        Collection<Request> myRequests = new ArrayList<>();
        Collections.addAll(myRequests, requests);
        return test(myRequests);
    }

    public Spider test(Collection<Request> requests) throws IOException {
        onStart();
        try {
            for (Request request : requests) {
                processRequest(request);
            }
        } finally {
            onStop();
        }
        return this;
    }

    private ResultItems processRequest(Request request) throws IOException {
        ResultItems page = request.getDownloader().download(request, this);
        if (page == null || page.getResource() == null) {
            addRequest(request);
            return page;
        }
        if (processValidate(page)) {
            throw new PageDeniedException();
        }
        if (request.getValidator() != null && request.getValidator().checkBeforeProcess(page)) {
            throw new PageDeniedException();
        }
        PageProcessor pageProcessor = request.getPageProcessor();
        if (pageProcessor != null) {
            pageProcessor.process(page);
        }
        if (request.getValidator() != null && request.getValidator().checkAfterProcess(page)) {
            throw new PageDeniedException();
        }
        doPipeline(page);
        onSuccess(request);
        addRequest(page);
        return page;
    }

    /**
     * check whether the resource is validation page.
     *
     * @return true if page is validation page.
     */
    private boolean processValidate(ResultItems page) {
        Object resource = page.getResource();
        if (!Document.class.isInstance(resource)) {
            return false;
        }
        Document doc = (Document) resource;
        if (!PageValidateHelper.isValidationPage(page.getRequest().getUrl(), doc)) {
            return false;
        }
        if (!page.isCacheUsed()) {
            LOG.info("meet validation page for " + page.getRequest().getUrl());
            proxyScheduler.onError(page.getRequest());
        }
        page.addTargetRequest(page.getRequest())
                .skip(true);
        return true;
    }

    private void doPipeline(ResultItems page) {
        for (Pipeline pipeline : pipelines) {
            pipeline.process(page);
        }
    }

    public void onError(Request request) {
        if (request == null) {
            return;
        }
        proxyScheduler.onError(request);
        listeners.forEach(e -> e.onError(this, request));
    }

    protected void onSuccess(Request request) {
        if (request == null) {
            return;
        }
        scheduler.markDown(request, this);
        proxyScheduler.onSuccess(request);
        listeners.forEach(e -> e.onSuccess(this, request));
    }

    private void onStart() {
        checkIfRunning();
        initComponent();
        doRegisterToMonitor();
        stat = Status.Running;
        pipelines.forEach(pipeline -> {
            try {
                pipeline.open();
            } catch (IOException e) {
                LOG.error(null, e);
            }
        });
        listeners.forEach(l -> l.onStart(this));
    }

    /**
     * 关闭打开着的文件句柄、流句柄、线程池
     */
    private void onStop() {
        pipelines.forEach(pipeline -> {
            try {
                pipeline.close();
            } catch (IOException e) {
                LOG.error(null, e);
            }
        });
        threadPool.shutdown();
        listeners.forEach(l -> l.onStop(this));
        stat = Status.Stopped;
    }

    public Spider addRequest(Collection<Request> requests) {
        if (requests == null) {
            return this;
        }
        requests.forEach(this::addRequest);
        return this;
    }

    private Spider addRequest(ResultItems page) {
        if (page == null) {
            return this;
        }
        page.getTargetRequests().forEach(this::addRequest);
        return this;
    }

    /**
     * If the request retry count is already bigger than 6, then no retry again, and is marked failed. Or else, increase the retry count, and push the request to scheduler.
     *
     * @param request if null, ignore it.
     */
    public Spider addRequest(Request request) {
        if (request == null) {
            return this;
        }
        if (retry(request)) {
            request.incrRetryCount();
            if (request.getRetryCount() > 0) {
                request.setCacheReadable(false); // If resubmit request, force to not read from cache.
                // request.setValidPeriod(0L); cannot use set valid period to 0, because if valid period is 0, the downloaded web page cannot be saved.
            }
            scheduler.push(request, this);
        }
        return this;
    }

    public boolean retry(Request request) {
        return request != null && request.getRetryCount() < ConfigManager.getInt("spider.download.maxRetry", 6);
    }
}
