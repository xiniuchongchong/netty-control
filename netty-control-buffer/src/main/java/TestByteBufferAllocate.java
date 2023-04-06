import java.nio.ByteBuffer;

/**
 * @author pancm
 * @date 2023年04月06日 2:53 PM
 */
public class TestByteBufferAllocate {

    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
        /**
         * 区别：
         * class java.nio.HeapByteBuffer   1.堆内存，读写效率较低， 2.涉及到垃圾回收，垃圾回收算法影响，GC时数据会变迁
         * class java.nio.DirectByteBuffer 1.直接内存，读写效率高（少一次拷贝）2.不受GC算法影响 3.缺点：受操作系统影响，内存分配效率低，使用不当会造成内存泄漏
         */
    }
}
