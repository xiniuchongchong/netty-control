package practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author pancm
 * @date 2023年05月05日 3:18 PM
 */
@Slf4j
public class EchoClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>(){
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.info(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .connect("localhost",8080)
                .sync()
                .channel();

        channel.closeFuture().addListener(future -> {
           group.shutdownGracefully();
           log.info("我关闭了");
        });

        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.nextLine();
                if(line.equals("q")){
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }

        }).start();

    }
}
