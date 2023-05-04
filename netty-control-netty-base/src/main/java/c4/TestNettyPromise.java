package c4;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author pancm
 * @date 2023年05月04日 2:25 PM
 */
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.转呗eventLoop
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //2.可以主动创建 Promise ,结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(()->{
            //3.任意一个线程计算完以后，向promise填充结果
            log.debug("开始计算");
            try {
                int i = 1/0;
                Thread.sleep(1000);
                //放置成功的结果
                promise.setSuccess(80);
            } catch (Exception e) {
                e.printStackTrace();
                //放置失败的结果
                promise.setFailure(e);
            }


        }).start();

        //4.接收结果的线程
        log.debug("等待结果.......");
        log.debug("结果为：{}",promise.get());
    }


}
