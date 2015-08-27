package chookin.chubot.server;

import chookin.chubot.proto.ChubotProtos.MasterProto;
import chookin.chubot.web.model.Agent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 8/14/15.
 */
@ChannelHandler.Sharable
public class ChubotServerHandler extends SimpleChannelInboundHandler<MasterProto> {
    private static final Logger LOG = Logger.getLogger(ChubotServerHandler.class);
    private final BlockingQueue<MasterProto> answer = new LinkedBlockingQueue<>();
    private final Map<Channel, Agent> agents = new HashMap<>();
    private final ReadWriteLock agentsLock = new ReentrantReadWriteLock();
    private final AtomicLong protoId = new AtomicLong();

    public void send(String methodName, String paras){
        MasterProto proto = MasterProto.newBuilder()
                .setId(protoId.incrementAndGet())
                .setMethod(methodName)
                .setParas(paras)
                .build();
        send(proto);
    }

    protected void send(MasterProto proto){
        for(Channel channel: channels()) {
            channel.writeAndFlush(proto);
        }
    }

    public List<Channel> channels(){
        agentsLock.readLock().lock();
        try{
            return new ArrayList<>(agents.keySet());
        }finally {
            agentsLock.readLock().unlock();
        }
    }

    public List<Agent> agents(){
        agentsLock.readLock().lock();
        try{
            return new ArrayList<>(agents.values());
        }finally {
            agentsLock.readLock().unlock();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        Agent agent = Agent.newOne().set("address", channel.remoteAddress().toString());
        agentsLock.writeLock().lock();
        try{
            agents.put(channel, agent);
        }finally {
            agentsLock.writeLock().unlock();
        }
        agent.save();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        Agent agent;
        agentsLock.writeLock().lock();
        try{
            agent = agents.get(channel);
            agents.remove(channel);
        }finally {
            agentsLock.writeLock().unlock();
        }
        agent.set("endTime", new Date()).update();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception{
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MasterProto masterProto) throws Exception {
        answer.add(masterProto);
    }
}
