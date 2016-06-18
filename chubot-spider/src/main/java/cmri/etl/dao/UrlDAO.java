package cmri.etl.dao;

import cmri.etl.common.UrlHash;
import cmri.utils.dao.MongoHandler;
import cmri.utils.web.UrlHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

/**
 * 存储已抓取的web page url.
 *
 * Created by chookin on 16/6/17.
 */
public class UrlDAO {
    protected UrlDAO() {
    }

    public static UrlDAO getInstance() {
        return new UrlDAO();
    }

    protected String getId(UrlHash entity) {
        return entity.getUrl();
    }

    protected BasicDBObject getBasicDBObject(UrlHash entity) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", getId(entity));
        doc.put("hash", entity.getHash());
        doc.put("time", new Date());
        return doc;
    }

    protected UrlHash parse(DBObject dbObject) {
        if (dbObject == null) return null;
        return new UrlHash((String) dbObject.get("_id"), (String) dbObject.get("hash"))
                ;
    }

    public void saveFetched(String url) {
        String hash = UrlHelper.getHash(url);
        MongoHandler.instance().updateOrInsert("urlFetched", getBasicDBObject(new UrlHash(url, hash)));
    }
}
