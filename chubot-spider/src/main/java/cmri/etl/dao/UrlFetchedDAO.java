package cmri.etl.dao;

import cmri.etl.common.UrlHash;
import cmri.utils.web.UrlHelper;
import cmri.utils.dao.MongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

/**
 * Created by zhuyin on 4/27/15.
 */
public class UrlFetchedDAO extends MongoDAO<UrlHash> {
    static final UrlFetchedDAO instance = new UrlFetchedDAO();
    public static UrlFetchedDAO getInstance() {
        return instance;
    }
    protected UrlFetchedDAO() {
        super("urlFetched");
    }

    @Override
    protected String getId(UrlHash entity) {
        return entity.getUrl();
    }

    @Override
    protected BasicDBObject getBasicDBObject(UrlHash entity) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", getId(entity));
        doc.put("hash", entity.getHash());
        doc.put("time", new Date());
        return doc;
    }

    @Override
    public UrlHash parse(DBObject dbObject) {
        if(dbObject == null) return null;
        return new UrlHash((String) dbObject.get("_id"), (String) dbObject.get("hash"))
        ;
    }

    public static void save(String url){
        UrlFetchedDAO dao = instance;
        try{
            String hash = UrlHelper.getHash(url);
            dao.save(new UrlHash(url, hash));
        }finally {
            dao.close();
        }
    }
}
