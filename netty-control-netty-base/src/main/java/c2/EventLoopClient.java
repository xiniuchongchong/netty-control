package c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author pancm
 * @date 2023年04月25日 2:21 PM
 *
 * 总结：一但与服务端进行连接，这个客户端的channel会与某一个服务端的eventLoop绑定，以后这个channel上的事件都会让这个eventLoop执行
 */
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {


        {
            //7.启动类
            Channel channel =  new Bootstrap()
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
                    .connect(new InetSocketAddress("localhost", 8080))
                    .sync()//13.阻塞方法，直到连接建立
                    .channel();//代表连接对象，客户端和服务端的SocketChannel
            System.out.println(channel);
            System.out.println("");
        }
    }
}
