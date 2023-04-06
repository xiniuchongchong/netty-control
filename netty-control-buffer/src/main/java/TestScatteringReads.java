import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.RandomAccess;

import static util.ByteBufferUtil.debugAll;

/**
 * @author pancm
 * @date 2023年04月06日 3:40 PM
 *
 *
 * 分散读取，有一个文本文件 words.txt
 * onetwothree
 * 使用如下方式读取，可以将数据填充至多个 buffer
 */
public class TestScatteringReads {

    public static void main(String[] args) {

        try(FileChannel channel = new RandomAccessFile("words.txt","r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1,b2,b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);


        }catch (Exception e){

        }

    }
}
