package c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pancm
 * @date 2023年05月05日 3:51 PM
 */
@Slf4j
public class HelloWorldServer {

        void start() {
            NioEventLoopGroup boss = new NioEventLoopGroup(1);
            NioEventLoopGroup worker = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.channel(NioServerSocketChannel.class);
                //设置系统的接收缓冲区大小（滑动窗口），小了以后会出现半包现象
               // serverBootstrap.option(ChannelOption.SO_RCVBUF,10);
                //调整netty的缓冲区大小（byteBuf）
                serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16));
                serverBootstrap.group(boss, worker);
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                });
                ChannelFuture channelFuture = serverBootstrap.bind(8080);
                log.debug("{} binding...", channelFuture.channel());
                channelFuture.sync();
                log.debug("{} bound...", channelFuture.channel());
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("server error", e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                log.debug("stoped");
            }
        }

    public static void main(String[] args) {
        new HelloWorldServer().start();
    }
}
