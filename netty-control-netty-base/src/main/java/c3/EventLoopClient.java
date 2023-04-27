package c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

/**
 * @author pancm
 * @date 2023年04月25日 3:39 PM
 */
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 2带有future 。Promise的类都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //1.连接到服务器
                //异步非阻塞，main发起了调用，真正执行 connect 的是 nio线程
                .connect("127.0.0.1", 8080);
        //2.1 同步方法阻塞住当前线程，知道nio线程建立完毕，如果不进行阻塞，就会继续向下执行获取channel，现在还没建立连接，没有获取到连接的channel，就发送不了数据
//        channelFuture.sync();
//        Channel channel = channelFuture.channel();
//        log.debug("{}",channel);
//        //向服务器发送数据
//        channel.writeAndFlush(new Date() + ": hello world!");
        //2.2使用addListener()方法异步处理结果  将等连接结果的工作交给nio线程
        channelFuture.addListener(new ChannelFutureListener() {
            @Override//在连接建立好以后，会调用此方法
            public void operationComplete(ChannelFuture future) throws Exception {
               Channel channel = future.channel();
               log.debug("{}",channel);
                //向服务器发送数据
               channel.writeAndFlush(new Date() + ": hello world!");

            }
        });

    }
}
