import lombok.extern.slf4j.Slf4j;
import util.ByteBufferUtil;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * @author pancm
 * @date 2023年04月03日 10:08 AM
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args){
        //file channel
        /**
         * 常用的 Channel 类有：
         * FileChannel、用于文件的数据读写
         * DatagramChannel
         * ServerSocketChannel(服务端) 和 SocketChannel(客户端)
         * DatagramChannel 用于 UDP 的数据读写
         * ServerSocketChannel 和 SocketChannel 用于 TCP 的 数据读写
         */
        //1.输入输出流，2.randomAccessFile
        try{
            FileChannel channel = new FileInputStream("data.txt").getChannel();
            //准备缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true){
                //从channel里向byteBuffer里写入数据
                int len = channel.read(byteBuffer);
                log.info("读到的字节数为{}",len);
                if(len == -1){
                    break;
                }
                byteBuffer.flip();//切换成读模式
                //判断是否还有数据
                while (byteBuffer.hasRemaining()){
                    byte b = byteBuffer.get();
                    log.info("实际字节：{}",(char) b);
                }
                byteBuffer.clear();//切换为写模式
            }

        } catch (Exception e){

        }

    }

}
