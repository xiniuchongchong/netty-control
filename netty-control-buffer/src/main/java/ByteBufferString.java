import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static util.ByteBufferUtil.debugAll;

/**
 * @author pancm
 * ByteBuffer的常见方法  ----操作字符串
 * @date 2023年04月06日 3:16 PM
 */
public class ByteBufferString {
    public static void main(String[] args) {

        ByteBuffer byteBuffer1 = ByteBuffer.allocate(16);

        //第一种
        byteBuffer1.put("hello".getBytes());
        debugAll(byteBuffer1);

        //第二种  直接切换到了读模式
        ByteBuffer byteBuffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(byteBuffer2);

        //第三种 nio提供的工具类 wrap  也是直接切换到了读模式
        ByteBuffer byteBuffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(byteBuffer3);

        //相反的方法
        String str1 = StandardCharsets.UTF_8.decode(byteBuffer2).toString();
        System.out.println(str1);

        //没有用flip方法，没有切换到读模式，啥也读不到
        String str2 = StandardCharsets.UTF_8.decode(byteBuffer1).toString();
        System.out.println(str2);
    }
}
