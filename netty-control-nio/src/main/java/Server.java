import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import static util.ByteBufferUtil.debugRead;

@Slf4j
public class Server {


public static void main(String[]args) throws IOException {

        //使用nio来理解阻塞模式  单线程
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        //2.绑定监听接口
        ssc.bind(new InetSocketAddress(8080));

        //3.客户端连接集合
        List<SocketChannel> list = new ArrayList<>();
        while (true){
             log.info("connecting.....");
             //4.accept 建立服务器的连接
             SocketChannel sc = ssc.accept();
             log.info("connected.....{}",sc);
             list.add(sc);
             for (SocketChannel channel:list) {
                 //接收客户端数据
                 log.info("before read.....{}",channel);
                 channel.read(buffer);
                 buffer.flip();
                 debugRead(buffer);
                 buffer.clear();
                 log.info("after read.....{}",channel);
             }
        }

      }

}
