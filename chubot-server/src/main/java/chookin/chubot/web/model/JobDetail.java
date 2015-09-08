package chookin.chubot.web.model;

import cmri.etl.job.JobMetric;
import com.jfinal.plugin.activerecord.Model;

import java.sql.Timestamp;

/**
 * Created by zhuyin on 8/31/15.
 */
public class JobDetail extends Model<JobDetail> {
    public static final JobDetail DAO = new JobDetail();
    public JobDetail(){}
    public JobDetail(JobMetric metric){
        this.set("id", metric.getId())
                .set("startTime", new Timestamp(metric.getStarTime().getTime()))
                .set("endTime", metric.getEndTime() == null ? null : new Timestamp(metric.getEndTime().getTime()))
                .set("status", metric.getStatus().toString());
    }
}
