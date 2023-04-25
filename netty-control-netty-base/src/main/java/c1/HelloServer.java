package c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * demo
 * @author pancm
 * @date 2023年04月25日 10:15 AM
 * description：服务端和客户端总体来说就是先组装初始化再调用
 *  一开始需要树立正确的观念
 *  把 channel 理解为数据的通道
 *  把 msg 理解为流动的数据，最开始输入是 ByteBuf，但经过 pipeline 的加工，会变成其它类型对象，最后输出又变成 ByteBuf
 *  把 handler 理解为数据的处理工序
 *    工序有多道，合在一起就是 pipeline，pipeline 负责发布事件（读、读取完成...）传播给每个 handler， handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
 *    handler 分 Inbound （入站）和 Outbound（出站） 两类
 *  把 eventLoop 理解为处理数据的工人
 *    工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
 *    工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 的待处理任务，任务分为普通任务、定时任务
 *    工人按照 pipeline 顺序，依次按照 handler 的规划（代码）处理数据，可以为每道工序指定不同的工人
 */
public class HelloServer {

    public static void main(String[] args) {
        //1.启动器 负责组装netty的组件
        new ServerBootstrap()
                 //2.类似前边的例子，bossEventLoop,workEventLoop(selector,thread)  group组
                .group(new NioEventLoopGroup())//一开始关心的是accept事件    16.由某个eventLoop处理read事件，接收到ByteBuf
                //3.选择 服务器的serverSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                //4.boss负责处理连接，worker（child）负责处理读写  ，决定了worker（child）能执行哪些操作（handler）
                .childHandler(
                        //5.channel 代表和客户端进行数据读写的通道 initializer初始化，负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    //12.连接建立以后 调用初始化方法
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //6.添加具体handler      17.将byteBuf转为字符串
                        ch.pipeline().addLast(new StringDecoder());//将byteBuf 转为字符串
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){//自定义handler
                            @Override//读事件    18.执行read方法，打印
                           public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
                                //19.打印上一步转化好的字符串
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
