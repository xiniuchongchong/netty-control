package c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author pancm
 * @date 2023年04月25日 10:47 AM
 */
public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        //7.启动类
        new Bootstrap()
                //8.添加EventLoop
                .group(new NioEventLoopGroup())
                //8.选择客户端的channel实现
                .channel(NioSocketChannel.class)
                //10.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override//12.连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new StringEncoder());
                                            //15.调用StringEncoder 把hello world 转为byteBuf
                    }
                })
                //11.连接到服务器
                .connect(new InetSocketAddress("localhost",8080))
                .sync()//13.阻塞方法，直到连接建立
                .channel()//代表连接对象，客户端和服务端的SocketChannel
                .writeAndFlush("hello world");//14.发送数据
    }
}
