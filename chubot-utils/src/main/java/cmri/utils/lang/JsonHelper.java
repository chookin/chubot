package cmri.utils.lang;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhuyin on 3/18/15.
 */
public class JsonHelper {
    private static final String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String toJson(Object obj){
        // return JSON.toJSONString(obj, SerializerFeature.WriteClassName);
        // return new Gson().toJson(obj);
        return new GsonBuilder()
                //.setDateFormat(datePattern)
                .registerTypeAdapter(Date.class, dateSer)
                .registerTypeAdapter(Date.class, dateDeser)
                .create()
                .toJson(obj);
    }

    public static <T> T parseObject(String json, Class<T> classOfT) {
        // return JSON.parseObject(json, classOfT);
        return new GsonBuilder()
                //.setDateFormat(datePattern)
                .registerTypeAdapter(Date.class, dateSer)
                .registerTypeAdapter(Date.class, dateDeser)
                .create()
                .fromJson(json, classOfT);
    }

    public static Set<String> parseStringSet(String json) {
        return new Gson().fromJson(json, new TypeToken<Set<String>>() {
        }.getType());
    }

    public static Map<String, String> parseStringMap(String json) {
        Map<String, String> map = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
        map.remove("@type");
        return map;
    }

    public static Map<String, Object> parseStringObjectMap(String json) {
        Map<String, Object> map = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
        map.remove("@type");
        return map;
    }

    // http://stackoverflow.com/questions/6873020/gson-date-format
    static final JsonSerializer<Date> dateSer = (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(src.getTime());

    static final JsonDeserializer<Date> dateDeser = (json, typeOfT, context) -> json == null ? null : new Date(json.getAsLong());

}
