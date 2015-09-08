package chookin.chubot.agent;

import chookin.chubot.common.ChuChannelInboundHandler;
import chookin.chubot.proto.ChubotProtos.MasterProto;
import cmri.etl.job.Job;
import cmri.etl.job.JobMetric;
import cmri.utils.lang.JsonHelper;
import cmri.utils.lang.LangHelper;
import cmri.utils.lang.SerializationHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by zhuyin on 8/14/15.
 */
public class ChubotAgentHandler extends ChuChannelInboundHandler{
    private final ReadWriteLock historyLock = new ReentrantReadWriteLock();
    private final AgentMetric metric;
    private final Collection<Job> historyJobs;
    private Channel channel;
    public ChubotAgentHandler(AgentMetric metric, Collection<Job> historyJobs) {
        super(6000000000000000000L);
        this.metric = metric;
        this.historyJobs = historyJobs;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channel = ctx.channel();
    }

    @Override
    protected ChuChannelInboundHandler send(MasterProto proto) {
        channel.writeAndFlush(proto);
        return this;
    }
    public void initAgent(ChannelHandlerContext ctx, MasterProto proto){
        Map<String, String> map = JsonHelper.parseStringMap(proto.getParas());
        boolean newOne = false;
        if(metric.getId()== -1){
            metric.setId(Integer.parseInt(map.get("id")));
            newOne = true;
        }else{
            map.put("id", String.valueOf(metric.getId()));
        }
        map.put("newOne", String.valueOf(newOne));
        map.put("startTime", String.valueOf(metric.getStarTime().getTime()));
        send(ctx, getProto("initAgent", JsonHelper.toJson(map), proto.getId()));
    }

    public void addJars(ChannelHandlerContext ctx, MasterProto proto){
        try {
            String jarsPath = proto.getParas();
            String[] arr = jarsPath.split("\\n|;");
            for(String jarPath: arr) {
                LangHelper.addJar2ClassLoader(jarPath);
                LOG.info("add "+jarPath + " to classpath");
            }
        } catch (IOException e) {
            LOG.error(null, e);
        }
    }
    public void commitJob(ChannelHandlerContext ctx, MasterProto proto){
        try {
            Job job = Job.createJob(JsonHelper.parseStringMap(proto.getParas())).start();
            historyLock.writeLock().lock();
            try {
                historyJobs.add(job);
            }finally {
                historyLock.writeLock().unlock();
            }
        } catch (ReflectiveOperationException e) {
            LOG.error(null, e);
        }
    }
    public void getJobs(ChannelHandlerContext ctx, MasterProto proto){
        String status = proto.getParas();
        JobMetric.Status myStatus;
        if(StringUtils.isBlank(status)){
            myStatus = null;
        }else {
            myStatus = JobMetric.Status.valueOf(status);
        }
        Collection<Job> jobs = getJobs(myStatus);
        ArrayList<JobMetric> metrics = new ArrayList<>(jobs.stream().map(Job::metric).collect(Collectors.toList()));
        // cannot convert the collection to json string, or else it will be failed to parse JobMetric items from the result json string.
        send(ctx, getProto("getJobs", SerializationHelper.serialize(metrics), proto.getId()));
    }

    Collection<Job> getJobs(JobMetric.Status status){
        historyLock.readLock().lock();
        try{
            if(status == null){
                return new ArrayList<>(historyJobs);
            }else {
                return historyJobs.stream().filter(job -> job.metric().getStatus().equals(status)).collect(Collectors.toList());
            }
        }finally {
            historyLock.readLock().unlock();
        }
    }
}
