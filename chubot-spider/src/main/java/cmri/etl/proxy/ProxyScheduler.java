package cmri.etl.proxy;

import cmri.etl.common.Request;

import java.io.Serializable;

/**
 * Created by zhuyin on 7/10/15.
 */
public interface ProxyScheduler extends Serializable {
    /**
     * Call this method when request final success
     * @return this
     */
    ProxyScheduler onSuccess(Request request);

    /**
     * Call this method when request fail
     * @return this
     */
    ProxyScheduler onError(Request request);

    /**
     * Get a proxy for the request
     * @return the available proxy
     */
    Proxy getProxy(Request request);
}
