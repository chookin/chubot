package chookin.chubot.agent;

import java.util.Date;

/**
 * Created by zhuyin on 8/31/15.
 */
public class AgentMetric {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public Status getStat() {
        return stat;
    }

    public void setStat(Status stat) {
        this.stat = stat;
    }

    private int id = -1;
    private Date starTime;
    private Status stat = Status.New;

    public enum Status {
        New,
        Inited,
        Connected,
        Disconnected,
        Stop,
        Stopped
    }
}
