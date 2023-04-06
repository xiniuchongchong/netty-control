import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author pancm
 * @date 2023年04月06日 4:27 PM
 *
 * 集中写入
 */
public class TestGatheringWrites {

    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");

        //向文件写入
        try(FileChannel channel = new RandomAccessFile("words2.txt","rw").getChannel()){
            channel.write(new ByteBuffer[]{b1,b2,b3});
        }catch (Exception e){

        }


    }
}
