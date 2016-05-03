package cmri.etl.job;

import cmri.etl.spider.Spider;

import java.util.Date;

/**
 * Created by zhuyin on 8/29/15.
 */
class JobMetricListener implements JobListener{
    private final Job job;
    private final JobMetric metric;

    public JobMetricListener(Job job){
        this.job = job;
        this.metric = new JobMetric();
    }

    @Override
    public void onInit(){
        metric.setStatus(JobMetric.Status.Inited);
    }
    @Override
    public void onStart() {
        metric.setStatus(JobMetric.Status.Running);
        metric.setStarTime(new Date());
    }
    @Override
    public void onSuccess() {
        metric.setStatus(JobMetric.Status.Succeeded);
        metric.setEndTime(new Date());
    }

    @Override
    public void onFail() {
        metric.setStatus(JobMetric.Status.Failed);
        metric.setEndTime(new Date());
    }

    public void onSpiderAdd(Spider spider){
        metric.addSpider(spider);
    }
    public JobMetric metric(){
        return metric;
    }
}