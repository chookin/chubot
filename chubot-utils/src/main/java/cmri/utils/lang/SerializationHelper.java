package cmri.utils.lang;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Created by zhuyin on 3/19/15.
 */
public class SerializationHelper {
    /**
     * Convert the object to base64 string.
     * @param obj
     * @return
     */
    public static String serialize(Serializable obj){
        byte[] bytes = SerializationUtils.serialize(obj);
        return Base64.encodeBase64String(bytes);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String objectString){
        byte[] bytes = Base64.decodeBase64(objectString);
        return (T) SerializationUtils.deserialize(bytes);
    }
}
