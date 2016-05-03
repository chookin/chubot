package cmri.etl.job;

import cmri.etl.common.Request;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderListener;

/**
 * Created by zhuyin on 8/29/15.
 */
public class SpidersListener implements SpiderListener{
    private final Job job;
    public SpidersListener(Job job){
        this.job = job;
    }

    @Override
    public void onStart(Spider spider) {
        this.job.addSpider(spider);
    }

    @Override
    public void onStop(Spider spider) {

    }

    @Override
    public void onSuccess(Spider spider, Request request) {

    }

    @Override
    public void onError(Spider spider, Request request) {

    }
}
