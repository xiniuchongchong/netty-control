package c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static util.ByteBufferUtil.debugAll;

/**
 * @author pancm
 * @date 2023年04月21日 4:37 PM
 *
 * 用多线程优化selector服务器
 */
@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss,0,null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        //1.创建固定数量的worker 并且初始化
        Worker worker = new Worker("worker-0");
        worker.register();
        while (true){
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}",sc.getRemoteAddress());
                    //2.关联selector
                    sc.register(worker.selector,SelectionKey.OP_READ,null);

                }
            }
        }
    }

   static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private boolean start = false;
        public Worker(String name){
            this.name = name;
        }

        //初始化线程和selector
        public void register() throws IOException {
            if(!start) {
                thread = new Thread(this, name);
                thread.start();
                selector = Selector.open();
                start = true;
            }

        }

        @Override
        public void run() {
            while (true){
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel sc = (SocketChannel) key.channel();
                            sc.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
