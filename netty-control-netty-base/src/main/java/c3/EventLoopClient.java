package c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
        //阻塞方法，如果不进行阻塞，就会继续向下执行获取channel，现在还没建立连接，就发送不了数据
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.debug("{}",channel);
        //2.向服务器发送数据
        channel.writeAndFlush(new Date() + ": hello world!");
    }
}
