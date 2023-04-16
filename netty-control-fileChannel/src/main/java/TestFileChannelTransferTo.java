import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author pancm
 * @date 2023年04月14日 1:41 PM
 *
 *测试两个channel传输数据
 */
public class TestFileChannelTransferTo {

    public static void main(String[] args) {
        try(FileChannel from = new FileInputStream("data.txt").getChannel();
            FileChannel to = new FileOutputStream("to.txt").getChannel()){
            //效率高，底层会用操作系统的零拷贝进行优化。  一次最大限制为2G，更多能容得多次传输

            long size = from.size();
            //单次传
            from.transferTo(0, size,to);
            //多次传 left代表还剩多少字节
            for (long left = size;left > 0;) {
                System.out.println("position:" + (size - left) + " left:" + left);
                //transferTo方法每次返回的就是还剩多少字节
                left -= from.transferTo(size - left,left,to);
                System.out.println("aaaa");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
