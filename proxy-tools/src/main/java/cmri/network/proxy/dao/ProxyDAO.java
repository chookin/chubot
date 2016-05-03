package cmri.network.proxy.dao;

import cmri.etl.proxy.Proxy;
import cmri.utils.dao.MongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Map;

/**
 * Created by zhuyin on 3/5/15.
 */
public class ProxyDAO extends MongoDAO<Proxy> {
    static final ProxyDAO instance = new ProxyDAO();

    public static ProxyDAO getInstance() {
        return instance;
    }

    protected ProxyDAO() {
        super("proxy");
    }

    @Override
    protected String getId(Proxy entity) {
        return entity.getId();
    }

    @Override
    protected BasicDBObject getBasicDBObject(Proxy entity) {
        BasicDBObject doc = new BasicDBObject();
        for (Map.Entry<String, Object> entry : entity.toStringMap().entrySet()) {
            if ("collection".equals(entry.getKey())) {
                continue;
            }
            doc.put(entry.getKey(), entry.getValue());
        }
        return doc;
    }

    @Override
    public Proxy parse(DBObject dbObject) {
        if (dbObject == null) return null;
        Proxy proxy = new Proxy()
                .setHost((String) dbObject.get("host"))
                .setPort((Integer) dbObject.get("port"))
                .setUser((String) dbObject.get("user"))
                .setPasswd((String) dbObject.get("passwd"));
        Object properties = dbObject.get("properties");
        if (properties instanceof BasicDBObject) {
            BasicDBObject myProperties = (BasicDBObject) properties;
            for (Map.Entry<String, Object> entry : myProperties.entrySet()) {
                proxy.set(entry.getKey(), entry.getValue());
            }
        }
        return proxy;
    }
}
