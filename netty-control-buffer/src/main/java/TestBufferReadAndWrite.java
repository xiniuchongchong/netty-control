
import java.nio.ByteBuffer;

import static util.ByteBufferUtil.debugAll;

/**
 * @author pancm
 * @date 2023年04月06日 10:29 AM
 * 测试byteBuffer的读写切换
 */
public class TestBufferReadAndWrite {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 0x61);
        debugAll(byteBuffer);
        byteBuffer.put(new byte[] {0x62,0x63,0x64});
        debugAll(byteBuffer);

        byteBuffer.flip();
        System.out.println(byteBuffer.get());
        debugAll(byteBuffer);
        byteBuffer.compact();
        debugAll(byteBuffer);
        byteBuffer.put(new byte[]{0x65,0x6f});
        debugAll(byteBuffer);
    }
}
