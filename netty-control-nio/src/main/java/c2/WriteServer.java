package c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 *
 * @description 可以向服务端写入数据
 * @author pancm
 * @date 2023年04月21日 10:42 AM
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8089));
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
           selector.select();
           System.out.println("selected.....");
           Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
           while (iterator.hasNext()){
               SelectionKey key = iterator.next();
               iterator.remove();
               if(key.isAcceptable()){
                   SocketChannel sc = ssc.accept();
                   sc.configureBlocking(false);
                   SelectionKey scKey = sc.register(selector,0,null);
                   scKey.interestOps(SelectionKey.OP_READ);
                   //1.向客户端发送内容
                   StringBuilder sb = new StringBuilder();
                   for (int i = 0; i < 3000000; i++) {
                       sb.append("a");
                   }
                   ByteBuffer buffer =Charset.defaultCharset().encode(sb.toString());
                   //2.返回实际写入的字节数
                   int write = sc.write(buffer);
                   System.out.println(write);
                   //3.判断是否有剩余内容
                   if (buffer.hasRemaining()){
                       //4.关注可写事件  这样写不会把读事件覆盖
                       scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE); //5表示可读可写  在原有关注事件的基础上，多关注 写事件
                       //5.把未写完的数据以附件的形式挂到scKey上
                       scKey.attach(buffer);
                   }
               }else if(key.isWritable()){
                   ByteBuffer buffer = (ByteBuffer) key.attachment();
                   SocketChannel sc = (SocketChannel) key.channel();
                   int write = sc.write(buffer);
                   System.out.println(write);
                   //6.清理操作
                   if(!buffer.hasRemaining()){
                       //清除buffer
                       key.attach(null);
                       key.interestOps(key.readyOps() - SelectionKey.OP_WRITE); //又变成了1
                   }
               }

           }
        }
    }
}
