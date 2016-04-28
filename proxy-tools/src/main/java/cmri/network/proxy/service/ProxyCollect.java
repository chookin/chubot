package cmri.network.proxy.service;

import cmri.etl.common.Request;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.pipeline.MongoPipeline;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.lang.BaseOper;

import java.util.Collection;

/**
 * Created by zhuyin on 3/25/15.
 */
public abstract class ProxyCollect extends BaseOper {
    @Override
    public boolean action() {
        return processSites();
    }

    boolean processSites() {
        new SpiderAdapter("proxy")
                .addPipeline(new FilePipeline(), new MongoPipeline("proxy"))
                .addRequest(getSeedProxyRequests())
                .run();
        return true;
    }

    public abstract Collection<Request> getSeedProxyRequests();
}
