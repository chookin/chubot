package chookin.chubot.web.model;

import cmri.utils.web.jfinal.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuyin on 8/26/15.
 */
@TableBind(tableName = "agent")
public class Agent extends Model<Agent> {
    private static final AtomicInteger idGen = new AtomicInteger();
    static {
        Integer id = ModelHelper.getMaxId("agent");
        if(id != null) idGen.set(id);
    }
    public Agent(){
    }

    public static Agent newOne(){
        return new Agent().set("id", idGen.incrementAndGet()).set("startTime", new Timestamp(System.currentTimeMillis()));
    }
    public static int nextId(){
        return idGen.incrementAndGet();
    }


    public static final Agent DAO = new Agent();
}
