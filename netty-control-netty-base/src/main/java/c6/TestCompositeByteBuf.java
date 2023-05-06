package c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static c6.TestByteBuf.log;

/**
 * 【零拷贝】的体现之一
 * @author pancm
 * @date 2023年05月05日 2:46 PM
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{6,7,8,9,10});

        //会发生两次数据拷贝
//        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
//        buffer.writeBytes(buf1).writeBytes(buf2);
//        log(buffer);


        //避免了内存复制，但是得重新计算读写指针
        CompositeByteBuf bufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        //true参数表示自动增长写指针
        bufs.addComponents(true,buf1,buf2);

    }
}
