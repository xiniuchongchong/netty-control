package c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static c6.TestByteBuf.log;

/**
 *
 * 【零拷贝】的体现之一
 * @author pancm
 * @date 2023年05月05日 2:30 PM
 */
public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);

        //在切片过程中没有发生数据复制
        ByteBuf buf1 = buf.slice(0,5);
        buf1.retain();//用retain方法，对内存引用计数加1,在原有内存释放后，就不会被真的回收
        ByteBuf buf2 = buf.slice(5,5);
        buf2.retain();
        log(buf1);
        log(buf2);

        //1.改变一个byteBuf，会改变原有的byteBuf
//        buf1.setByte(0,'b');
//        log(buf1);
//        log(buf);

        //释放原有内存会报错，因为跟原始的byteBuf是同一块内存，可以在Buf1上用retain方法，对内存引用计数加1
        System.out.println("释放原有ByteBuf内存");
        buf.release();
        log(buf1);

        buf1.release();
        buf2.release();





    }
}
