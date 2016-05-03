package cmri.etl.job;

import cmri.etl.spider.Spider;
import cmri.utils.lang.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuyin on 8/29/15.
 */
public class JobMetric implements Serializable {
    public long getId() {
        return id;
    }

    public JobMetric setId(long id) {
        this.id = id;
        return this;
    }

    public Date getStarTime() {
        return starTime;
    }

    public JobMetric setStarTime(Date starTime) {
        this.starTime = starTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public JobMetric setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public JobMetric addSpider(Spider spider){
        spiders.add(new Pair<>(spider.uuid(), spider.name()));
        return this;
    }
    public JobMetric setStatus(Status status){
        this.status = status;
        return this;
    }

    public Status getStatus(){
        return this.status;
    }

    private long id = -1;
    private Date starTime;
    private Date endTime;
    private Status status = Status.New;
    private final List<Pair<String,String>> spiders = new ArrayList<>();
    public enum Status {
        /**
         * 作业初始状态。当一个作业被创建时初始状态被置为NEW
         */
        New,
        /**
         * 作业经初始化后的状态
         */
        Inited,
        /**
         * 作业运行状态
         */
        Running,
        /**
         * 作业运行成功
         */
        Succeeded,
        /**
         * 作业运行失败所处的状态
         */
        Failed;
    }
}
