import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pancm
 * @date 2023年04月14日 2:37 PM
 * 1.遍历目录,统计目录下的目录和文件的总数
 *
 */
public class TestFileWalkFileTree {

    public static void main(String[] args) throws IOException {

    }
    //统计多级目录文件和目录数量
    public static void m1() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk"),new SimpleFileVisitor<Path>(){
            //前置处理器
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println(dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dir count:"+ dirCount);
        System.out.println("file count:"+fileCount);



    }
    //统计多级目录jar包数量
    public static void m2() throws IOException {
            AtomicInteger jarCount = new AtomicInteger();
            Files.walkFileTree(Paths.get("/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    if (file.toFile().getName().endsWith(".jar")) {
                        jarCount.incrementAndGet();
                    }
                    return super.visitFile(file, attrs);
                }
            });
            System.out.println(jarCount);
        }
    //删除多级目录
    public static void m3() throws IOException{
        Path path = Paths.get(".....");
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            //后置处理器
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    //拷贝多级目录
    public static void m4() throws IOException{
        long start = System.currentTimeMillis();
        String source = "......";
        String target = ".......";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 是目录
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                }
                // 是普通文件
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }


}
