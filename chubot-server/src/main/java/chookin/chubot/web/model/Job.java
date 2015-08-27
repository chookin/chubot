package chookin.chubot.web.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuyin on 8/20/15.
 */
public class Job extends Model<Job> {
    private static final AtomicInteger idGen = new AtomicInteger();
    static {
        Integer id = ModelHelper.getMaxId("job");
        if(id != null) idGen.set(id);
    }

    /**
     * public for reflection by ModelBuilder
     */
    public Job(){
    }

    public static Job newOne(){
        return new Job().set("id", idGen.incrementAndGet()).set("time", new Date());
    }

    public static final Job DAO = new Job();
}
