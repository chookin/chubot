package cmri.etl.validator;

import cmri.etl.common.ResultItems;

import java.io.Serializable;

/**
 * Check whether the acquired resource is a access denied page.
 *
 * Created by chookin on 16/5/3.
 */
public interface PageValidator extends Serializable {
    /**
     * Check before processing the web resource
     *
     * @return true if it is confirmed that the resource is a access denied page, which may be a login page, a validation page, or a error page that has no valid data.
     */
    boolean checkBeforeProcess(ResultItems page);

    /**
     * Check after processing the web resource
     *
     * @return true if it is confirmed that the resource is a access denied page, which may be a login page, a validation page, or a error page that has no valid data.
     */
    boolean checkAfterProcess(ResultItems page);
}
