import java.nio.ByteBuffer;

import static util.ByteBufferUtil.debugAll;

/**
 * @author pancm
 * @date 2023年04月06日 3:03 PM
 */
public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put(new byte[]{'a','b','c','d'});
        byteBuffer.flip();


        byteBuffer.get(new byte[4]);
        debugAll(byteBuffer);
        //rewind 方法从头开始读
        byteBuffer.rewind();
        System.out.println( (char) byteBuffer.get());

        //mark(回到标记点) 和reset方法（回到标记点）
        byteBuffer.mark();
        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());
        byteBuffer.reset();
        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());

        //get(i)方法，不会改变position的位置
        System.out.println(byteBuffer.get(3));



    }
}
