package cmri.etl.common;

import java.util.Map;

/**
 * Created by zhuyin on 6/24/15.
 */
public interface MapItem extends IdItem {
    /**
     * Convert to map of string and object.
     * @return the result map.
     */
    Map<String, Object> toStringMap();
}
