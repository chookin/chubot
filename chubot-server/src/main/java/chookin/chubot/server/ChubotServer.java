package chookin.chubot.server;

import chookin.chubot.proto.ChubotProtos;
import cmri.utils.configuration.ConfigManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

/**
 * Created by zhuyin on 8/14/15.
 */
public class ChubotServer {
    private static final Logger LOG = Logger.getLogger(ChubotServer.class);
    private int port = ConfigManager.getAsInteger("server.port", 58000);
    private ChannelFuture cf;
    private final ChubotServerHandler handler = new ChubotServerHandler();
    // We are implementing a server-side application in this example, and therefore two NioEventLoopGroup will be used. The first one, often called 'boss', accepts an incoming connection. The second one, often called 'worker', handles the traffic of the accepted connection once the boss accepts the connection and registers the accepted connection to the worker.
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final ChubotServer instance = new ChubotServer();

    public static ChubotServer instance(){return instance;}

    private ChubotServer(){}

    public ChubotServer port(int port){
        this.port = port;
        return this;
    }

    public int port(){
        return port;
    }

    public ChubotServerHandler handler(){
        return handler;
    }

    public ChubotServer start() throws InterruptedException {
        if(cf != null && cf.channel().isOpen()){
            return this;
        }
        LOG.info("start chubot server at port " + port);
        try {
            // ServerBootstrap is a helper class that sets up a server.
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                            // Here, we specify to use the NioServerSocketChannel class which is used to instantiate a new Channel to accept incoming connections.
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerChannelInitializer(handler))
                            //  option() is for the NioServerSocketChannel that accepts incoming connections. childOption() is for the Channels accepted by the parent ServerChannel, which is NioServerSocketChannel in this case.
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000);
            // Bind and start to accept incoming connections.
            cf = b.bind(port).sync();
            return this;
        }catch (InterruptedException e){
            stop();
            throw e;
        }
    }

    public ChubotServer stop(){
        LOG.info("stop chubot server");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        return this;
    }
    /**
     * 阻塞的方式启动
     */
    public void run() throws InterruptedException {
        run();
        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        cf.channel().closeFuture().sync();
    }

    public static void main(String args[]){
        try {
            new ChubotServer().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ServerChannelInitializer extends ChannelInitializer<SocketChannel>{
        private ChubotServerHandler handler;
        ServerChannelInitializer(ChubotServerHandler handler){
            this.handler = handler;
        }
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast("frameDecoder", new ProtobufVarint32FrameDecoder())
                    .addLast("protobufDecoder", new ProtobufDecoder(ChubotProtos.MasterProto.getDefaultInstance()))
                    .addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender())
                    .addLast("protobufEncoder", new ProtobufEncoder())
                    .addLast("handler", handler);
        }
    }
}
