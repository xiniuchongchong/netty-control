package c1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static util.ByteBufferUtil.debugRead;

/**
 * @author pancm
 * @date 2023年04月18日 1:39 PM
 */
@Slf4j
public class NonBlockingServer {


    /**
     * nio非阻塞模式演示
     * @param args
     * @throws IOException
     *
     * 总结：虽然不阻塞了，但是线程一直在进行监听，即使没有连接，线程也在持续监听干活，没有数据连接时，没有读取时也在工作
     */
    public static void main(String[]args) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(20);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); //ServerSocketChannel的非阻塞与阻塞模式开关，默认是true
        //2.绑定监听接口
        ssc.bind(new InetSocketAddress(8080));

        //3.客户端连接集合
        List<SocketChannel> list = new ArrayList<>();
        while (true){
            //4.accept 建立服务器的连接
            SocketChannel sc = ssc.accept(); //非阻塞模式下，线程不会停止,会持续工作，sc的accept会返回null，
            if(sc != null){
                log.info("connected.....{}",sc);
                sc.configureBlocking(false); //SocketChannel的非阻塞与阻塞模式开关，默认是true
                list.add(sc);
            }

            for (SocketChannel channel:list) {
                //接收客户端数据
                int read = channel.read(buffer);  //非阻塞模式下，线程不会停止,会持续工作
                if(read > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.info("after read.....{}", channel);
                }
            }
        }
    }
}
