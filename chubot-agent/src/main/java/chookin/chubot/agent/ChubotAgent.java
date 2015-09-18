package chookin.chubot.agent;

import chookin.chubot.proto.ChubotProtos;
import cmri.etl.job.Job;
import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.DateHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by zhuyin on 8/14/15.
 */
public class ChubotAgent implements Runnable{
    private static final Logger LOG;

    static {
        try {
            System.setProperty("hostname.time", InetAddress.getLocalHost().getHostName() + "-" + DateHelper.toString(new Date(), "yyyyMMddHHmmss"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOG = Logger.getLogger(ChubotAgent.class);
    }
    private final EventLoopGroup group = new NioEventLoopGroup();
    // Bootstrap is similar to ServerBootstrap except that it's for non-server channels such as a client-side or connectionless channel.
    private final Bootstrap b = new Bootstrap();
    private String serverHost = ConfigManager.get("server.host", "127.0.0.1");
    private int serverPort = ConfigManager.getAsInteger("server.port", 58000);
    /**
     * If failed to connect server, then retry after an interval. Unit is milliseconds.
     */
    private int retryInterval = ConfigManager.getAsInteger("retry.interval", 10000);
    private final AgentMetric metric = new AgentMetric();
    private final Collection<Job> historyJobs = new ArrayList<>();

    private ChubotAgent init(){
        // Configure the client.
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new AgentChannelInitializer(metric, historyJobs))
        ;
        metric.setStat(AgentMetric.Status.Inited);
        return this;
    }
    private ChubotAgent connectSever() throws InterruptedException {
        ChannelFuture f = b.connect(serverHost, serverPort)
                .sync();
        metric.setStat(AgentMetric.Status.Connected);
        LOG.info("success to connect server "+serverHost+":"+serverPort);
        // Wait until the connection is closed.
        f.channel().closeFuture().sync();
        return this;
    }
    /**
     * 异步方式启动agent
     */
    public ChubotAgent start() {
        metric.setStarTime(new Date());
        Thread thread = new Thread(this);
        // 当所有的非守护线程结束时，程序也就终止了，同时会杀死进程中的所有守护线程。反过来说，只要任何非守护线程还在运行，程序就不会终止。
        thread.setDaemon(false);
        thread.start();
        return this;
    }


    @Override
    public void run() {
        try {
            init();
            while (!Thread.currentThread().isInterrupted() && metric.getStat() != AgentMetric.Status.Stop) {
                try {
                    connectSever();
                } catch (Throwable e) {
                    metric.setStat(AgentMetric.Status.Disconnected);
                }
                LOG.info("retry to connect server["+serverHost+":"+serverPort + "] after " + retryInterval/1000.0 + " seconds");
                ThreadHelper.sleep(retryInterval);
            }
        } catch (Throwable e) {
            LOG.error(null, e);
        }finally {
            onStop();
        }
    }

    public ChubotAgent stop(){
        metric.setStat(AgentMetric.Status.Stop);
        return this;
    }

    public ChubotAgent onStop(){
        // Shut down the event loop to terminate all threads.
        group.shutdownGracefully();
        metric.setStat(AgentMetric.Status.Stopped);
        return this;
    }

    public static void main(String[] args) throws Exception {
        new ChubotAgent().start();
    }

    static class AgentChannelInitializer extends ChannelInitializer<SocketChannel>{
        private final AgentMetric metric;
        private final Collection<Job> historyJobs;
        public AgentChannelInitializer(AgentMetric metric, Collection<Job> historyJobs){
            this.metric = metric;
            this.historyJobs = historyJobs;
        }
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
                    .addLast("protobufDecoder", new ProtobufDecoder(ChubotProtos.MasterProto.getDefaultInstance()))
                    .addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender())
                    .addLast("protobufEncoder", new ProtobufEncoder())
                    .addLast("handler", new ChubotAgentHandler(metric, historyJobs))
            ;
        }
    }


}
