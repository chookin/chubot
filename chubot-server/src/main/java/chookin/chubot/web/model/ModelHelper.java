package chookin.chubot.web.model;

import cmri.utils.web.jfinal.tablebind.TableBind;
import cmri.utils.dao.JdbcDAO;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * Created by zhuyin on 8/27/15.
 */
@TableBind(tableName = "agent")
public class ModelHelper {
    private static final Logger LOG = Logger.getLogger(ModelHelper.class);
    public static Integer getMaxId(String table) {
        try {
            return (Integer) new JdbcDAO().executeQuery("select max(id) as id from "+table, rs -> {
                        if (rs.next()){
                            return rs.getInt(1);
                        }else {
                            return null;
                        }
                    }
            );
        } catch (SQLException e) {
            LOG.fatal("fail to get max id of table '" + table + "'", e);
            System.exit(-1);
            return null;
        }
    }
}
