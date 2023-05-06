package c1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author pancm
 * @date 2023年05月06日 2:29 PM
 */
public class TestLengthFieldDecoder {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
              new LengthFieldBasedFrameDecoder(1024,0,4,1,4),
              new LoggingHandler(LogLevel.DEBUG)
        );
        //4个字节的长度，实际内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer,"Hello, World");
        send(buffer,"Hi!");
        channel.writeInbound(buffer);
    }

    public static void send(ByteBuf buffer,String content){
        byte[] bytes = content.getBytes();
        int length = bytes.length;
        buffer.writeInt(length);
        buffer.writeByte(1);
        buffer.writeBytes(bytes);
    }


}
