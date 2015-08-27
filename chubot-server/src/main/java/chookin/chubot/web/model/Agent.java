package chookin.chubot.web.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuyin on 8/26/15.
 */
public class Agent extends Model<Agent> {
    private static final AtomicInteger idGen = new AtomicInteger();
    static {
        Integer id = ModelHelper.getMaxId("agent");
        if(id != null) idGen.set(id);
    }
    public Agent(){
    }

    public static Agent newOne(){
        return new Agent().set("id", idGen.incrementAndGet()).set("startTime", new Date());
    }

    public static final Agent DAO = new Agent();
}
