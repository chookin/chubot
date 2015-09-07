package chookin.chubot.common;

import chookin.chubot.proto.ChubotProtos;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 8/31/15.
 */
public abstract class ChuChannelInboundHandler extends SimpleChannelInboundHandler<ChubotProtos.MasterProto> {
    protected static final Logger LOG = Logger.getLogger(ChuChannelInboundHandler.class);
    private final Map<Long, BlockingQueue<ChubotProtos.MasterProto>> cache = new HashMap<>();
    private final ReadWriteLock cacheLock =new ReentrantReadWriteLock();
    private final AtomicLong protoId;
    public ChuChannelInboundHandler(long initId){
        this.protoId = new AtomicLong(initId);
    }
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ChubotProtos.MasterProto masterProto) throws Exception {
        LOG.trace("masterProto -> " + masterProto);
        if(cache(masterProto)){
        }else {
            invoke(ctx, masterProto);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error(null, cause);
        ctx.close();
    }

    protected boolean cache(ChubotProtos.MasterProto masterProto){
        cacheLock.readLock().lock();
        try{
            if(cache.containsKey(masterProto.getId())){
                cache.get(masterProto.getId()).add(masterProto);
                return true;
            }else {
                return false;
            }
        }finally {
            cacheLock.readLock().unlock();
        }
    }
    protected void invoke(ChannelHandlerContext ctx, ChubotProtos.MasterProto masterProto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String method = masterProto.getMethod();
        this.getClass().getMethod(method, ChannelHandlerContext.class, ChubotProtos.MasterProto.class).invoke(this, ctx, masterProto);
    }

    protected ChubotProtos.MasterProto sendSync(ChubotProtos.MasterProto proto) throws InterruptedException {
        regist(proto.getId());
        try {
            send(proto);
            return cache.get(proto.getId()).take();
        }finally {
            unRegist(proto.getId());
        }
    }
    protected ChubotProtos.MasterProto sendSync(Channel channel, ChubotProtos.MasterProto proto) throws InterruptedException {
        regist(proto.getId());
        try {
            channel.writeAndFlush(proto);
            return cache.get(proto.getId()).take();
        }finally {
            unRegist(proto.getId());
        }
    }
    protected ChubotProtos.MasterProto sendSync(ChannelHandlerContext ctx, ChubotProtos.MasterProto proto) throws InterruptedException {
        regist(proto.getId());
        try {
            ctx.writeAndFlush(proto);
            return cache.get(proto.getId()).take();
        }finally {
            unRegist(proto.getId());
        }
    }
    protected abstract ChuChannelInboundHandler send(ChubotProtos.MasterProto proto);
    protected ChuChannelInboundHandler send(Channel channel, ChubotProtos.MasterProto proto){
        channel.writeAndFlush(proto);
        return this;
    }
    protected ChuChannelInboundHandler send(ChannelHandlerContext ctx, ChubotProtos.MasterProto proto){
        ctx.writeAndFlush(proto);
        return this;
    }

    protected ChubotProtos.MasterProto getProto(String methodName, String paras){
        return  getProto(methodName, paras, protoId.incrementAndGet());
    }

    protected ChubotProtos.MasterProto getProto(String methodName, String paras, long protoId){
        return  ChubotProtos.MasterProto.newBuilder()
                .setId(protoId)
                .setMethod(methodName)
                .setParas(paras == null? "": paras)
                .build();
    }

    private BlockingQueue<ChubotProtos.MasterProto> regist(long key){
        cacheLock.writeLock().lock();
        try {
            BlockingQueue<ChubotProtos.MasterProto> queue = cache.get(key);
            if(queue == null) {
                cache.put(key, new ArrayBlockingQueue<>(1));
            }
            return queue;
        }finally {
            cacheLock.writeLock().unlock();
        }
    }

    private ChuChannelInboundHandler unRegist(long key){
        cacheLock.writeLock().lock();
        try{
            cache.remove(key);
            return this;
        }finally {
            cacheLock.writeLock().unlock();
        }
    }
}
