package c4;


import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class TestNettyFuture {
    public static void main(String[] args){
        NioEventLoopGroup group = new NioEventLoopGroup();
        //每个eventLoop持有一个线程
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 70;
            }
        });
         //同步方法获取
//        log.debug("等待结果");
//        log.debug("结果为：{}", future.get());

        //异步方法获取
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("接收结果：{}", future.getNow());
            }
        });
    }

}
