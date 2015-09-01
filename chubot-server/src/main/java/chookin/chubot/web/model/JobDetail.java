package chookin.chubot.web.model;

import cmri.etl.job.JobMetric;
import com.jfinal.plugin.activerecord.Model;

/**
 * Created by zhuyin on 8/31/15.
 */
public class JobDetail extends Model<JobDetail> {
    public static final JobDetail DAO = new JobDetail();
    public JobDetail(){}
    public JobDetail(JobMetric metric){
        this.set("id", metric.getId())
                .set("startTime", metric.getStarTime())
                .set("endTime", metric.getEndTime())
                .set("status", metric.getStatus().toString());
    }
}
