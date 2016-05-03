package cmri.etl.scheduler;

import cmri.etl.common.Request;
import cmri.etl.common.Task;
import cmri.utils.lang.SerializationHelper;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;

/**
 * Every spider should has its own PriorityScheduler.<br>
 * Created by zhuyin on 3/5/15.
 */
public interface Scheduler extends Serializable {
    /**
     * Add a url to fetch
     */
    Scheduler push(Request request, Task task);

    /**
     * Get an url to crawl
     */
    Request poll(Task task);

    /**
     * Mark a request has been successfully process, and no need to fetch.
     */
    Scheduler markDown(Request request, Task task);

    /**
     * Empty wait requests set and done requests set of this task.
     */
    Scheduler clear(Task task);

    /**
     * cannot only use {@link Request#getUrl} as key, because for some requests which have the same urls, they can be differ by header or cookie, or even request data.
     */
    default byte[] getKey(Request request) {
        return DigestUtils.md5(SerializationHelper.serialize(request));
    }

    default String getKeyHex(Request request) {
        return DigestUtils.md5Hex(SerializationHelper.serialize(request));
    }
}
