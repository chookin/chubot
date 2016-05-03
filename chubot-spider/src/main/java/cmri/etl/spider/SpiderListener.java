package cmri.etl.spider;

import cmri.etl.common.Request;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by zhuyin on 5/24/15.
 */
public interface SpiderListener extends Serializable{
    default Logger getLogger(){
        return Logger.getLogger(SpiderListener.class);
    }
    /**
     * 处理spider启动事件
     */
    void onStart(Spider spider);

    /**
     * 处理spider停止事件，在此刻关闭文件句柄，输出最终统计结果等。
     */
    void onStop(Spider spider);

    /**
     * 处理请求成功
     */
    void onSuccess(Spider spider, Request request);

    /**
     * 处理请求失败
     */
    void onError(Spider spider, Request request);
}
