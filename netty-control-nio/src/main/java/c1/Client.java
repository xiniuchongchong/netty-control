package c1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author pancm
 * @date 2023年04月18日 2:08 PM
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        System.out.println("waiting...."); //这里可以用 debug模式的 Evaluate Expression 执行表达式向客户端发送数据(sc.write(Charset.defaultCharset().encode("hello!"));)
        //执行完以后server端会从read方法继续运行
        //注意：第二次执行表达式后,server端不会再响应，因为线程会阻塞在accept方法上，只有建立新的连接（新的客户端连接）后，线程才会继续向下执行

    }
}