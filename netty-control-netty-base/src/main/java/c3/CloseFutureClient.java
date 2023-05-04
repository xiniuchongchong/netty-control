package c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author pancm
 * @date 2023年04月27日 10:07 AM
 */
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture =  new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override//12.连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost",8080));
        Channel channel = channelFuture.sync().channel();
        log.debug("{}",channel);
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.nextLine();
                if("q".equals(line)){
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();
        //获取ClosedFuture对象 1）同步处理关闭 2）异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        //1)第一种
//        log.debug("waiting close...");
//        closeFuture.sync();
//        log.debug("处理关闭之后的操作");
        //2)第二种
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("处理关闭之后的操作");
                //优雅的停下线程
                group.shutdownGracefully();
            }
        });
    }
}
