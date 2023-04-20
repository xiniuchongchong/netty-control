package c1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static util.ByteBufferUtil.debugRead;

/**
 * selector 应用
 * @author pancm
 * @date 2023年04月18日 2:34 PM
 */
@Slf4j
public class SelectorServer {

    /**
     * selector演示
     * @param args
     * @throws IOException
     *
     */
    public static void main(String[]args) throws IOException {

        //1.创建selector
        Selector selector = Selector.open();
        //2.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //3.注册socketChannel
        //SelectionKey就是未来事件发生后，通过它知道事件和哪个channel的事件
        //事件有四种：1.connect 2.accept  3.read  4.write
        SelectionKey selectionKey = ssc.register(selector,0,null);//参数0表示不关注任何事件
        log.info("register key:{}",selectionKey);
        //告诉selector对哪个事件感兴趣
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        while (true){
            //4.selector的select方法，没有事件发生，线程会阻塞，有事件线程就会继续执行
            //在事件未处理时，它不会阻塞,事件发生后要不取消（key.cancel()方法），要不就处理
            selector.select();
            //5.触发事件的集合,返回集合内部包含所有发生的事件
            //事件处理完以后，这个被触发的事件集合不会自动删除SelectionKey
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println("SelectionKey的集合大小："+keys.size());
            Iterator<SelectionKey> iterator = keys.iterator();//可能会有多种事件，所以下边得根据不同的时间类型做判断
            log.debug("keys集合大小:{}",keys.size());
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                log.debug("key:{}",key);
                //处理事件
                if(key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("sc:{}", sc);
                    log.debug("scKey:{}", scKey);
                }else if(key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();//拿到触发事件的channel
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        int read = channel.read(buffer);
                        if(read == -1){
                            key.cancel();
                        }else {
                            buffer.flip();
                            System.out.println(Charset.defaultCharset().decode(buffer));
                            //debugRead(buffer);
                        }
                    }catch (IOException ie){
                        ie.printStackTrace();
                        key.cancel(); //因为客户端断开连接了，所以要将key在selector集合里删除掉
                    }
                }
                //处理完事件以后得自己删除，否则不会主动删除，下次遍历还会遍历到
                iterator.remove();
            }
        }
    }
}
