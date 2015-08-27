package chookin.chubot.agent;

import chookin.chubot.proto.ChubotProtos;
import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.configuration.ConfigManager;
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

/**
 * Created by zhuyin on 8/14/15.
 */
public class ChubotAgent implements Runnable{
    private static final Logger LOG = Logger.getLogger(ChubotAgent.class);
    private final EventLoopGroup group = new NioEventLoopGroup();
    // Bootstrap is similar to ServerBootstrap except that it's for non-server channels such as a client-side or connectionless channel.
    private final Bootstrap b = new Bootstrap();
    private Status stat = Status.New;
    private String serverHost = ConfigManager.get("server.host", "127.0.0.1");
    private int serverPort = ConfigManager.getAsInteger("server.port", 58000);
    /**
     * If failed to connect server, then retry after an interval. Unit is milliseconds.
     */
    private int retryInterval = 30000;

    private ChubotAgent init(){
        // Configure the client.
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new AgentChannelInitializer())
        ;
        stat = Status.Inited;
        return this;
    }
    private ChubotAgent connectSever() throws InterruptedException {
        ChannelFuture f = b.connect(serverHost, serverPort)
                .sync();
        stat = Status.Connected;
        LOG.info("success to connect server "+serverHost+":"+serverPort);
        // Wait until the connection is closed.
        f.channel().closeFuture().sync();
        return this;
    }
    /**
     * 异步方式启动agent
     */
    public ChubotAgent start() {
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
            while (!Thread.currentThread().isInterrupted() && stat != Status.Stop) {
                try {
                    connectSever();
                } catch (Throwable e) {
                    stat = Status.Disconnected;
                }
                LOG.info("retry to connect server after " + retryInterval/1000.0 + " seconds");
                ThreadHelper.sleep(retryInterval);
            }
        } catch (Throwable e) {
            LOG.error(null, e);
        }finally {
            onStop();
        }
    }

    public ChubotAgent stop(){
        stat = Status.Stop;
        return this;
    }

    public ChubotAgent onStop(){
        // Shut down the event loop to terminate all threads.
        group.shutdownGracefully();
        stat = Status.Stopped;
        return this;
    }

    public static void main(String[] args) throws Exception {
        new ChubotAgent().start();
    }

    static class AgentChannelInitializer extends ChannelInitializer<SocketChannel>{
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
                    .addLast("protobufDecoder", new ProtobufDecoder(ChubotProtos.MasterProto.getDefaultInstance()))
                    .addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender())
                    .addLast("protobufEncoder", new ProtobufEncoder())
                    .addLast("handler", new ChubotAgentHandler())
            ;
        }
    }

    public enum Status {
        New,
        Inited,
        Connected,
        Disconnected,
        Stop,
        Stopped
    }
}
