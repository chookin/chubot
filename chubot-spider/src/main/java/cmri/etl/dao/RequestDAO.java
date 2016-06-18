package cmri.etl.dao;

import cmri.etl.common.Request;
import cmri.utils.dao.MongoHandler;
import cmri.utils.lang.JsonHelper;
import cmri.utils.lang.SerializationHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 存储{@link Request}
 * <p>
 * Created by chookin on 16/6/17.
 */
public class RequestDAO {
    public static final String COLLECTION_REQUEST_FAILED = "requestFailed";

    protected RequestDAO() {
    }

    public static RequestDAO getInstance() {
        return new RequestDAO();
    }

    /**
     * 必须存储{@link Request},因为一个web资源请求不仅包括url,还包括cookie等数据
     *
     * @param request 需要存储的请求
     */
    public void saveFailed(Request request) {
        MongoHandler.instance().updateOrInsert(COLLECTION_REQUEST_FAILED, getBasicDBObject(request));
    }

    public void removeFromFailed(Request request) {
        MongoHandler.instance().remove(COLLECTION_REQUEST_FAILED, getId(request));
    }

    public List<Request> getFailed() {

        return MongoHandler.instance().find(COLLECTION_REQUEST_FAILED, new HashMap<>())
                .stream()
                .map(this::parse)
                .collect(Collectors.toList());
    }


    private String getId(Request entity) {
        // id字段中不应该包含retryCount字段
        // return DigestUtils.md5Hex(SerializationHelper.serialize(entity));
        return DigestUtils.md5Hex(entity.getUrl() + entity.getHeaders() + entity.getCookies() + entity.getData());
    }

    private BasicDBObject getBasicDBObject(Request entity) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", getId(entity));
        doc.put("url", entity.getUrl());
        doc.put("downloader", entity.getDownloader().getClass().getName());
        doc.put("pageProcessor", entity.getPageProcessor().getClass().getName());
        doc.put("priority", entity.getPriority());
        doc.put("retryCount", entity.getRetryCount());
        doc.put("properties", JsonHelper.toJson(entity.getExtra()));
        doc.put("request", SerializationHelper.serialize(entity));
        doc.put("time", new Date());
        return doc;
    }

    private Request parse(DBObject dbObject) {
        if (dbObject == null) return null;
        if (!dbObject.containsField("request")) {
            return null;
        }
        return SerializationHelper.deserialize((String) dbObject.get("request"));
    }
}
