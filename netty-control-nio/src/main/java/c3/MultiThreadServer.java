package c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
        //创建固定数量的worker
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];//availableProcessors可以获取本机cpu的核心数，但是在docker里有问题（不会获取容器的核心数，还是获取的本机的）
        for (int i = 0; i <workers.length ; i++) {
            workers[i] = new Worker("worker-"+i);
        }
        AtomicInteger index = new AtomicInteger(0);
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
                    log.debug("before register...{}",sc.getRemoteAddress());
                    //用取模的方式进行负载均衡
                    workers[index.getAndIncrement() % workers.length].register(sc);//初始化selector 启动worker
                    log.debug("after register...{}",sc.getRemoteAddress());
                }
            }
        }
    }

    //加上static的目的是为了能new，不加static属于成员内部类，不能new，加上变成静态内部类
   static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        private volatile boolean start = false;  //多线程保证可见性
        public Worker(String name){
            this.name = name;
        }

        //初始化线程和selector
        public void register(SocketChannel sc) throws IOException {
            //一个worker只初始化一次
            if(!start) {
                thread = new Thread(this, name);
                thread.start();
                selector = Selector.open();
                start = true;
            }
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
           selector.wakeup();//用boss线程唤醒selector
        }

        @Override
        public void run() {
            while (true){
                try {
                    selector.select(); //阻塞方法
                    Runnable task = queue.poll();
                    if(task != null) {
                        task.run();  //执行 sc.register(selector,SelectionKey.OP_READ,null);方法
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if(key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel sc = (SocketChannel) key.channel();
                            log.debug(" has read...{}",sc.getRemoteAddress());
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
