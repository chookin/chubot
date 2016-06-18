package cmri.etl.job;

import cmri.etl.spider.Spider;
import cmri.utils.configuration.OptionsPack;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhuyin on 8/25/15.
 */
public abstract class SpiderJob implements Job {
    protected final Logger LOG = Logger.getLogger(SpiderJob.class);
    protected final OptionsPack options = new OptionsPack();
    /**
     * 用于监听并记录Job的启动时间,结束时间,状态(运行中,运行成功,运行失败等)等信息.
     */
    private JobMetricListener metricListener = new JobMetricListener(this);
    /**
     * 用于把Job的初始化,启动,成功,失败等事件通知出去
     */
    private final Collection<JobListener> listeners = new CopyOnWriteArrayList<>();
    /**
     * 该Job所启动的爬虫作业
     */
    private final Collection<Spider> spiders = new CopyOnWriteArrayList<>();
    /**
     * 用于Spider能方便地注册到一个Job中
     */
    private final SpidersListener spidersListener = new SpidersListener(this);

    public SpiderJob() {
        addListener(metricListener);
    }

    @Override
    public Job init(Map<String, String> options) {
        if (options == null) {
            throw new IllegalArgumentException("options is null");
        }
        /**
         * 通过id区分job
         */
        if (!options.containsKey("id")) {
            LOG.warn("no 'id' in options: " + options);
        } else {
            int id = Integer.valueOf(options.get("id"));
            this.metricListener.metric().setId(id);
        }
        this.options.put(options);
        listeners.forEach(JobListener::onInit);
        return this;
    }

    public Job addListener(JobListener listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * 异步方式启动job
     */
    @Override
    public Job start() {
        if(this.spiders.isEmpty()){
            prepare();
        }
        Thread thread = new Thread(this);
        // 当所有的非守护线程结束时，程序也就终止了，同时会杀死进程中的所有守护线程。反过来说，只要任何非守护线程还在运行，程序就不会终止。
        thread.setDaemon(false);
        thread.start();
        return this;
    }

    @Override
    public Job stop() {
        this.spiders.forEach(Spider::stop);
        return this;
    }

    @Override
    public void run() {
        try {
            onStart();
            this.spiders.forEach(Spider::run);
            onSuccess();
        } catch (Throwable t) {
            onFail();
            LOG.error(null, t);
        }
    }

    @Override
    public JobMetric getMetric() {
        return metricListener.metric();
    }

    @Override
    public OptionsPack getOptions() {
        return options;
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

    protected void onStart() {
        listeners.forEach(JobListener::onStart);
    }

    protected void onSuccess() {
        listeners.forEach(JobListener::onSuccess);
    }

    protected void onFail() {
        listeners.forEach(JobListener::onFail);
    }

    protected SpidersListener spidersListener() {
        return this.spidersListener;
    }

    /**
     * 按照添加次序顺序执行
     */
    protected Job addSpider(Spider spider) {
        spider.init(options.options())
                .addListener(this.spidersListener); // 让Job能自动知道该Spider的状态,即Spider的启动,停止,成功,失败等事件通过监听器通知到Job
        this.spiders.add(spider);
        this.metricListener.onSpiderAdd(spider);
        return this;
    }

    public Collection<Spider> spiders() {
        return spiders;
    }
}
