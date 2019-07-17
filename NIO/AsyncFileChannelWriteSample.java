import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class AsyncFileChannelWriteSample {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.wrap(("The win keeps Nadal at the top of the heap in men's"
                + "tennis, at least for a few more weeks. The world No2, Novak Djokovic, dumped out here in the"
                + "semi-finals by a resurgent Federer, will come hard at them again at Wimbledon but there is"
                + "much to come from two rivals who, for seven years, have held all pretenders at bay.").getBytes());
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.WRITE)) {
            Future<Integer> result = asynchronousFileChannel.write(buffer, 100);
            while (!result.isDone()) {
                System.out.println("Do something else while writing ...");
            }
            System.out.println("Written done: " + result.isDone());
            System.out.println("Bytes written: " + result.get());
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public static void fileLock(String[] args) {
        ByteBuffer buffer = ByteBuffer.wrap("Argentines At Home In Buenos Aires Cathedral\n The".getBytes());
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "CopaClaro.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.WRITE)) {
            Future<FileLock> featureLock = asynchronousFileChannel.lock();
            System.out.println("Waiting for the file to be locked ...");
            FileLock lock = featureLock.get();
            // or, use shortcut
            // FileLock lock = asynchronousFileChannel.lock().get();
            if (lock.isValid()) {
                Future<Integer> featureWrite = asynchronousFileChannel.write(buffer, 0);
                System.out.println("Waiting for the bytes to be written ...");
                int written = featureWrite.get();
                // or, use shortcut
                // int written = asynchronousFileChannel.write(buffer,0).get();
                System.out.println("I’ve written " + written + " bytes into " + path.getFileName() + " locked file!");
                lock.release();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    static Thread current;

    public static void fileLockCompletionHandler(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "CopaClaro.txt");
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            current = Thread.currentThread();
            asynchronousFileChannel.lock("Lock operation status:", new CompletionHandler<FileLock, Object>() {
                @Override
                public void completed(FileLock result, Object attachment) {
                    System.out.println(attachment + " " + result.isValid());
                    if (result.isValid()) {
                        // ... processing ...
                        System.out.println("Processing the locked file ...");
                        // ...
                        try {
                            result.release();
                        } catch (IOException ex) {
                            System.err.println(ex);
                        }
                    }
                    current.interrupt();
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println(attachment);
                    System.out.println("Error:" + exc);
                    current.interrupt();
                }
            });
            System.out.println("Waiting for file to be locked and process ... \n");
            try {
                current.join();
            } catch (InterruptedException e) {
            }
            System.out.println("\n\nClosing everything and leave! Bye, bye ...");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}