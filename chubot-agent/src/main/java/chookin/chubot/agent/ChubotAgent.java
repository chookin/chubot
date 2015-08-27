package chookin.chubot.agent;

import chookin.chubot.proto.ChubotProtos;
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

/**
 * Created by zhuyin on 8/14/15.
 */
public class ChubotAgent {
    public static void main(String[] args) throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Bootstrap is similar to ServerBootstrap except that it's for non-server channels such as a client-side or connectionless channel.
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new AgentChannelInitializer())
                    ;

            // Start the client.
            ChannelFuture f = b.connect(ConfigManager.get("server.host", "127.0.0.1"), ConfigManager.getAsInteger("server.port", 58000))
                    .sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
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
}
