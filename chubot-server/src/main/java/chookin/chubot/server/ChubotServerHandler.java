package chookin.chubot.server;

import chookin.chubot.common.ChuChannelInboundHandler;
import chookin.chubot.proto.ChubotProtos.MasterProto;
import chookin.chubot.web.model.Agent;
import chookin.chubot.web.model.JobDetail;
import cmri.etl.job.JobMetric;
import cmri.utils.lang.JsonHelper;
import cmri.utils.lang.MapAdapter;
import cmri.utils.lang.SerializationHelper;
import cmri.utils.lang.StringHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by zhuyin on 8/14/15.
 */
@ChannelHandler.Sharable
public class ChubotServerHandler extends ChuChannelInboundHandler{
    private final Map<Channel, Agent> agents = new HashMap<>();
    private final ReadWriteLock agentsLock = new ReentrantReadWriteLock();
    private final Set<String> jars = new ConcurrentHashSet<>();

    public ChubotServerHandler(){
        super(0);
    }
    public ChuChannelInboundHandler addJars(String jars){
        this.jars.add(jars);
        return send(getProto("addJars", jars));
    }

    public void initAgent(ChannelHandlerContext ctx, MasterProto proto){
        Map<String, String> map = JsonHelper.parseStringMap(proto.getParas());
        int id = Integer.parseInt(map.get("id"));
        Timestamp startTime = new Timestamp(Long.parseLong(map.get("startTime")));
        Agent agent = new Agent().set("id", id)
                .set("startTime", startTime);
        addChannel(ctx.channel(), agent, Boolean.parseBoolean(map.get("newOne")));
    }

    public void commitJob(String para) throws InterruptedException {
        Validate.notBlank(para, "para");
        Map<String, String> map = JsonHelper.parseStringMap(para);
        if ("true".equals(map.get("Singleton") )) {
            Channel channel = oneChannel();
            if(channel != null){
                send(channel, getProto("commitJob", para));
            }
        }else{
            send(getProto("commitJob", para));
        }
    }

    public Collection<Agent> agents(){
        agentsLock.readLock().lock();
        try{
            return new ArrayList<>(agents.values());
        }finally {
            agentsLock.readLock().unlock();
        }
    }
    public Collection<JobDetail> getJobs(int agentId, String status) throws InterruptedException {
        Channel channel = channel(agentId);
        if(channel == null){
            return new ArrayList<>();
        }
        String jobsStr = sendSync(channel, getProto("getJobs", status)).getParas();
        Collection<JobMetric> jobs = SerializationHelper.deserialize(jobsStr);
        return  jobs.stream().map(JobDetail::new).collect(Collectors.toList());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        int id = Agent.nextId();
        String paras = new MapAdapter<String,String>()
                .put("id", String.valueOf(id))
                .toJson();
        send(ctx, getProto("initAgent", paras));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        removeChannel(channel);
    }

    @Override
    protected ChubotServerHandler send(MasterProto proto){
        for(Channel channel: channels()) {
            channel.writeAndFlush(proto);
        }
        return this;
    }

    private List<Channel> channels(){
        agentsLock.readLock().lock();
        try{
            return new ArrayList<>(agents.keySet());
        }finally {
            agentsLock.readLock().unlock();
        }
    }

    private Channel oneChannel(){
        agentsLock.readLock().lock();
        try{
            return agents.keySet().iterator().next();
        }finally {
            agentsLock.readLock().unlock();
        }
    }

    /**
     *
     * @return null if nut found.
     */
    private Channel channel(int agentId){
        agentsLock.readLock().lock();
        try{
            for(Map.Entry<Channel, Agent> entry: agents.entrySet()){
                if(entry.getValue().getInt("id") == agentId){
                    return entry.getKey();
                }
            }
            return null;
        }finally {
            agentsLock.readLock().unlock();
        }
    }

    private ChubotServerHandler addChannel(Channel channel, Agent agent, boolean isNew){
        agent.set("address", channel.remoteAddress().toString());
        agentsLock.writeLock().lock();
        try{
            agents.put(channel, agent);
        }finally {
            agentsLock.writeLock().unlock();
        }
        if(!jars.isEmpty()){
            channel.writeAndFlush(getProto("addJars", StringHelper.join(jars,";")));
        }
        if(isNew) {
            agent.save();
        }else{
            agent.update();
        }
        return this;
    }

    private ChubotServerHandler removeChannel(Channel channel){
        Agent agent;
        agentsLock.writeLock().lock();
        try{
            agent = agents.get(channel);
            agents.remove(channel);
        }finally {
            agentsLock.writeLock().unlock();
        }
        if(agent != null) {
            agent.set("endTime", new Date()).update();
        }
        return this;
    }
}
