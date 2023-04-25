package c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static util.ByteBufferUtil.debugAll;

/**
 * AIO 用文件的例子
 * @author pancm
 * @date 2023年04月23日 2:41 PM
 */
@Slf4j
public class AioFileChannel {
    public static void main(String[] args) throws IOException {
       try {
           AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ);
           //参数1.ByteBuffer
           //参数2.读取的位置
           //参数3.附件
           //参数4.回调对象
           ByteBuffer buffer = ByteBuffer.allocate(16);
           log.debug("read begin.....");
           //这里的异步线程是守护线程，主线程结束以后就会结束
           channel.read(buffer, 0, null, new CompletionHandler<Integer, ByteBuffer>(){
               @Override
               public void completed(Integer result, ByteBuffer attachment) {
                   log.debug("read completed...",result);
                   buffer.flip();
                   debugAll(buffer);
               }
               @Override
               public void failed(Throwable exc, ByteBuffer attachment) {
                  exc.printStackTrace();
               }
           });
           log.debug("read end....");
       }catch (Exception e){
         e.printStackTrace();
       }
       System.in.read();

    }

}
