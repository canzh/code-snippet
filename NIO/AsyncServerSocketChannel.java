import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncServerSocketChannel {
    public static void main(String[] args) {
        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";
        // create an asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()) {
            if (asynchronousServerSocketChannel.isOpen()) {
                // set some options
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                // bind the asynchronous server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));// display a waiting
                                                                                              // message while ...
                                                                                              // waiting clients
                System.out.println("Waiting for connections ...");
                while (true) {
                    Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel
                            .accept();
                    try (AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get()) {
                        System.out.println("Incoming connection from: " + asynchronousSocketChannel.getRemoteAddress());
                        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                        // transmitting data
                        while (asynchronousSocketChannel.read(buffer).get() != -1) {
                            buffer.flip();
                            asynchronousSocketChannel.write(buffer).get();
                            if (buffer.hasRemaining()) {
                                buffer.compact();
                            } else {
                                buffer.clear();
                            }
                        }
                        System.out.println(asynchronousSocketChannel.getRemoteAddress() + " was successfully served!");
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                    }
                }
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void acceptMultiple(String[] args) {
        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";
        ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        // create asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()) {
            if (asynchronousServerSocketChannel.isOpen()) {
                // set some options
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                // bind the server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));
                // display a waiting message while ... waiting clients
                System.out.println("Waiting for connections ...");
                while (true) {
                    Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel
                            .accept();
                    try {
                        final AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture
                                .get();
                        Callable<String> worker = new Callable<String>() {
                            @Override
                            public String call() throws Exception {
                                String host = asynchronousSocketChannel.getRemoteAddress().toString();
                                System.out.println("Incoming connection from: " + host);
                                final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                                // transmitting data
                                while (asynchronousSocketChannel.read(buffer).get() != -1) {
                                    buffer.flip();
                                    asynchronousSocketChannel.write(buffer).get();
                                    if (buffer.hasRemaining()) {
                                        buffer.compact();
                                    } else {
                                        buffer.clear();
                                    }
                                }
                                asynchronousSocketChannel.close();
                                System.out.println(host + " was successfully served!");
                                return host;
                            }
                        };
                        taskExecutor.submit(worker);
                    } catch (InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                        System.err.println("\n Server is shutting down ...");
                        // this will make the executor accept no new threads
                        // and finish all existing threads in the queue
                        taskExecutor.shutdown();
                        // wait until all threads are finished
                        while (!taskExecutor.isTerminated()) {
                        }
                        break;
                    }
                }
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void serverBasedOnCompletionHandler(String[] args) {
        final int DEFAULT_PORT = 5555;
        final String IP = "127.0.0.1";
        // create an asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()) {
            if (asynchronousServerSocketChannel.isOpen()) {
                // set some options
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                // bind the server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP, DEFAULT_PORT));
                // display a waiting message while ... waiting clients
                System.out.println("Waiting for connections ...");
                asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

                    @Override
                    public void completed(AsynchronousSocketChannel result, Void attachment) {
                        asynchronousServerSocketChannel.accept(null, this);
                        try {
                            System.out.println("Incoming connection from: " + result.getRemoteAddress());
                            // transmitting data
                            while (result.read(buffer).get() != -1) {
                                buffer.flip();
                                result.write(buffer).get();
                                if (buffer.hasRemaining()) {
                                    buffer.compact();
                                } else {
                                    buffer.clear();
                                }
                            }
                        } catch (IOException | InterruptedException | ExecutionException ex) {
                            System.err.println(ex);
                        } finally {
                            try {
                                result.close();
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        asynchronousServerSocketChannel.accept(null, this);
                        throw new UnsupportedOperationException("Cannot accept connections!");
                    }
                });
                // Wait
                System.in.read();
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}