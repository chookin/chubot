package chookin.chubot.agent;

import chookin.chubot.proto.ChubotProtos.MasterProto;
import cmri.etl.common.Job;
import cmri.utils.lang.JsonHelper;
import cmri.utils.lang.LangHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by zhuyin on 8/14/15.
 */
public class ChubotAgentHandler extends SimpleChannelInboundHandler<MasterProto> {
    private static final Logger LOG = Logger.getLogger(ChubotAgentHandler.class);
    private final List<Job> historyJobs = new ArrayList<>();
    private final ReadWriteLock historyLock = new ReentrantReadWriteLock();
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MasterProto masterProto) throws Exception {
        LOG.info(masterProto);
        String method = masterProto.getMethod();
        this.getClass().getMethod(method, String.class).invoke(this, masterProto.getParas());
    }

    public void commitJob(String paras){
        try {
            Job job = Job.createJob(JsonHelper.parseStringMap(paras)).start();
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

    public void addJar(String jarPath){
        try {
            LangHelper.addJar2ClassLoader(jarPath);
        } catch (IOException e) {
            LOG.error(null, e);
        }
    }

    public Collection<Job> getJobs(Job.Status status){
        historyLock.readLock().lock();
        try{
            return historyJobs.stream().filter(job -> job.status().equals(status)).collect(Collectors.toList());
        }finally {
            historyLock.readLock().unlock();
        }
    }
}
