import java.nio.ByteBuffer;

import static util.ByteBufferUtil.debugAll;

/**
 * @author pancm
 * @date 2023年04月14日 9:56 AM
 */
public class TestByteBufferExam {

    /**
     * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
     * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
     *
     * * Hello,world\n
     * * I'm zhangsan\n
     * * How are you?\n
     *
     * 变成了下面的两个 byteBuffer (黏包(由于效率，一起发送，就会产生黏包现象)，半包（服务器大小限制的原因，一次性就能接收到"Ho",下边就得下次接收了）)
     *
     * * Hello,world\nI'm zhangsan\nHo
     * * w are you?\n
     *
     * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
     */

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);

    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //get(i)方法不改变position的位置
            if(source.get(i) == '\n'){  //学习编解码的时候有比"=="更好的办法
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                //向target里写
                for (int j = 0; j < length; j++) {
                    //注意get（）方法会改变position的位置
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }

}
