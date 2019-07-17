import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncFileChannelReadSample {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        String encoding = System.getProperty("file.encoding");
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ)) {
            Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
            while (!result.isDone()) {
                System.out.println("Do something else while reading ...");
            }
            System.out.println("Read done: " + result.isDone());
            System.out.println("Bytes read: " + result.get());
        } catch (Exception ex) {
            System.err.println(ex);
        }
        buffer.flip();
        System.out.print(Charset.forName(encoding).decode(buffer));
        buffer.clear();
    }

    public static void readFutureTimeout(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        int bytesRead = 0;
        Future<Integer> result = null;
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ)) {
            result = asynchronousFileChannel.read(buffer, 0);
            bytesRead = result.get(1, TimeUnit.NANOSECONDS);
            if (result.isDone()) {
                System.out.println("The result is available!");
                System.out.println("Read bytes: " + bytesRead);
            }
        } catch (Exception ex) {
            if (ex instanceof TimeoutException) {
                if (result != null) {
                    result.cancel(true);
                }
                System.out.println("The result is not available!");
                System.out.println("The read task was cancelled ? " + result.isCancelled());
                System.out.println("Read bytes: " + bytesRead);
            } else {
                System.err.println(ex);
            }
        }
    }

    static Thread current;

    public static void readCompletionHandler(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ)) {
            current = Thread.currentThread();
            asynchronousFileChannel.read(buffer, 0, "Read operation status ...",
                    new CompletionHandler<Integer, Object>() {
                        @Override
                        public void completed(Integer result, Object attachment) {
                            System.out.println(attachment);
                            System.out.print("Read bytes: " + result);
                            current.interrupt();
                        }

                        @Override
                        public void failed(Throwable exc, Object attachment) {
                            System.out.println(attachment);
                            System.out.println("Error:" + exc);
                            current.interrupt();
                        }
                    });
            System.out.println("\nWaiting for reading operation to end ...\n");
            try {
                current.join();
            } catch (InterruptedException e) {
            }
            // now the buffer contains the read bytes
            System.out.println("\n\nClose everything and leave! Bye, bye ...");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    static final Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");

    public static void readCompletionHandlerObjectParam(String[] args) {
        CompletionHandler<Integer, ByteBuffer> handler = new CompletionHandler<Integer, ByteBuffer>() {
            String encoding = System.getProperty("file.encoding");

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                System.out.println("Read bytes: " + result);
                attachment.flip();
                System.out.print(Charset.forName(encoding).decode(attachment));
                attachment.clear();
                current.interrupt();
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println(attachment);
                System.out.println("Error:" + exc);
                current.interrupt();
            }
        };
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ)) {
            current = Thread.currentThread();
            ByteBuffer buffer = ByteBuffer.allocate(100);
            asynchronousFileChannel.read(buffer, 0, buffer, handler);
            System.out.println("Waiting for reading operation to end ...\n");
            try {
                current.join();
            } catch (InterruptedException e) {
            }
            // the buffer was passed as attachment
            System.out.println("\n\nClosing everything and leave! Bye, bye ...");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    private static Set withOptions() {
        final Set options = new TreeSet<>();
        options.add(StandardOpenOption.READ);
        return options;
    }

    public static void openWithThreadPool(String[] args) {
        final int THREADS = 5;
        ExecutorService taskExecutor = Executors.newFixedThreadPool(THREADS);
        String encoding = System.getProperty("file.encoding");
        List<Future<ByteBuffer>> list = new ArrayList<>();
        int sheeps = 0;
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, withOptions(),
                taskExecutor)) {
            for (int i = 0; i < 50; i++) {
                Callable<ByteBuffer> worker = new Callable<ByteBuffer>() {
                    @Override
                    public ByteBuffer call() throws Exception {
                        ByteBuffer buffer = ByteBuffer.allocateDirect(ThreadLocalRandom.current().nextInt(100, 200));
                        asynchronousFileChannel.read(buffer, ThreadLocalRandom.current().nextInt(0, 100));
                        return buffer;
                    }
                };
                Future<ByteBuffer> future = taskExecutor.submit(worker);
                list.add(future);
            }
            // this will make the executor accept no new threads
            // and finish all existing threads in the queue
            taskExecutor.shutdown();
            // wait until all threads are finished
            while (!taskExecutor.isTerminated()) {
                // do something else while the buffers are prepared
                System.out.println("Counting sheep while filling up some buffers! So far I counted: " + (sheeps += 1));
            }
            System.out.println("\nDone! Here are the buffers:\n");
            for (Future<ByteBuffer> future : list) {
                ByteBuffer buffer = future.get();
                System.out.println("\n\n" + buffer);
                System.out.println("______________________________________________________");
                buffer.flip();
                System.out.print(Charset.forName(encoding).decode(buffer));
                buffer.clear();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}