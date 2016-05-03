package cmri.etl.processor;

import cmri.etl.common.ResultItems;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by zhuyin on 3/2/15.
 */
public interface PageProcessor extends Serializable {
    default Logger getLogger(){
        return Logger.getLogger(PageProcessor.class);
    }

    /**
     * Process the page, extract urls to fetch, extract the data and store
     *
     * @param page object contains downloaded web resource and processed result.
     */
    void process(ResultItems page);
}
