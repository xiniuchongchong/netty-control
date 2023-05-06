package c3.protocol;

import c3.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author pancm
 * @date 2023年05月06日 4:18 PM
 */
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {

        EmbeddedChannel channel = new EmbeddedChannel(
                //为了防止出现黏包，半包的现象
                //不能抽成单独的对象供多个channel使用，线程不安全，会记录消息状态，可能会把不同的eventLoop里的消息拼到一起   能不能抽成单独对象要看有没有@sharable注解
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(),
                new MessageCodec());
        //测试encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan","123","张三");
        //channel.writeOutbound(message);

        //测试decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buf);

        //测试半包黏包现象
        //如果不加LengthFieldBasedFrameDecoder 解码器，就会报错，加上以帧解码器会等字节获取够，才会走到LoggingHandler 处理器，打印日志
        ByteBuf s1 = buf.slice(0,100);
        ByteBuf s2 = buf.slice(100,buf.readableBytes() -100);
        s1.retain();//引用计数为2，加上以后内存s1不会真正释放,
        channel.writeInbound(s1);//inBound方法会释放内存，不加上边的retain，下边再调会报错
        channel.writeInbound(s2);

    }
}
