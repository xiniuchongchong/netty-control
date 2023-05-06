package c3.protocol;

import c3.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author pancm
 * @date 2023年05月06日 5:51 PM
 */
@Slf4j
@ChannelHandler.Sharable
/**
 * 无状态才能加@Sharable注解
 * 必须和LengthFieldBasedFrameDecoder一起使用，确保消息是完整的
 */
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        //1. 4个字节的魔数
        out.writeBytes(new byte[]{1,2,3,4});
        //2. 1 字节的版本
        out.writeByte(1);
        //3. 1 序列化方式  0 jdk, 1 json
        out.writeByte(0);
        //4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        //5. 4个字节的请求序号
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);//对齐填充用的字节，因为为了凑2的n次方倍的字节
        //6. 获取内容的字节数组进行序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);//将内容序列化
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //7. 长度
        out.writeInt(bytes.length);
        //8.写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int  magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int  sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes,0,length);
        //反序列化
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{},{},{},{},{},{}",magicNum,version,serializerType,messageType,sequenceId,length);
        log.debug("{}",message);
        //传递到下一个handler进行处理
        out.add(message);

    }
}
