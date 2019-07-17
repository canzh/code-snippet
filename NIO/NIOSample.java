import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

// https://github.com/qidingxuan/nio-1/tree/master/src/main/java/ibmdw/nio

public class NIOSample {
    public void floatBufferSample() {
        // 创建FloatBuffer，容量10,单位 float，也就是说创建的 buffer 放的下10个 float
        FloatBuffer buffer = FloatBuffer.allocate(10);

        for (int i = 0; i < buffer.capacity(); ++i) {
            float f = (float) Math.sin((((float) i) / 10) * (2 * Math.PI));
            buffer.put(f); // position ++
        }

        buffer.flip(); // position = 0

        while (buffer.hasRemaining()) {
            float f = buffer.get(); // position ++
            System.out.println(f);
        }
    }

    public void copyFile(String[] args) {
        // The following inner loop summarizes the process of using a buffer to copy
        // data from an input channel to an output channel.
        if (args.length < 2) {
            System.err.println("Usage: java CopyFile infile outfile");
            System.exit(1);
        }

        String infile = args[0];
        String outfile = args[1];

        // 从流中获取通道
        FileInputStream fin = new FileInputStream(infile);
        FileOutputStream fout = new FileOutputStream(outfile);

        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (true) {
            buffer.clear();
            int r = fcin.read(buffer);
            if (r == -1) {
                break;
            }
            buffer.flip();
            fcout.write(buffer);
        }
    }

    public void fastCopyFileUsingDirectBuffer(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java FastCopyFile infile outfile");
            System.exit(1);
        }

        String infile = args[0];
        String outfile = args[1];

        FileInputStream fin = new FileInputStream(infile);
        FileOutputStream fout = new FileOutputStream(outfile);

        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while (true) {
            buffer.clear();

            int r = fcin.read(buffer);

            if (r == -1) {
                break;
            }

            buffer.flip();

            fcout.write(buffer);
        }
    }

    public void memoryMappedFile(String args[]) throws Exception {
        RandomAccessFile raf = new RandomAccessFile("usemappedfile.txt", "rw");
        FileChannel fc = raf.getChannel();

        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1024);

        mbb.put(0, (byte) 97);
        mbb.put(1023, (byte) 122);

        raf.close();
    }

    static private final int firstHeaderLength = 2;
    static private final int secondHeaderLength = 4;
    static private final int bodyLength = 6;

    public void scatterGather(String args[]) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java UseScatterGather port");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        ServerSocketChannel ssc = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        ssc.socket().bind(address);

        int messageLength = firstHeaderLength + secondHeaderLength + bodyLength;

        ByteBuffer buffers[] = new ByteBuffer[3];
        buffers[0] = ByteBuffer.allocate(firstHeaderLength);
        buffers[1] = ByteBuffer.allocate(secondHeaderLength);
        buffers[2] = ByteBuffer.allocate(bodyLength);

        SocketChannel sc = ssc.accept();

        while (true) {

            // Scatter-read into buffers
            int bytesRead = 0;
            while (bytesRead < messageLength) {
                long r = sc.read(buffers);
                bytesRead += r;

                System.out.println("r " + r);
                for (int i = 0; i < buffers.length; ++i) {
                    ByteBuffer bb = buffers[i];
                    System.out.println("b " + i + " " + bb.position() + " " + bb.limit());
                }
            }

            // Process message here

            // Flip buffers
            for (int i = 0; i < buffers.length; ++i) {
                ByteBuffer bb = buffers[i];
                bb.flip();
            }

            // Scatter-write back out
            long bytesWritten = 0;
            while (bytesWritten < messageLength) {
                long r = sc.write(buffers);
                bytesWritten += r;
            }

            // Clear buffers
            for (int i = 0; i < buffers.length; ++i) {
                ByteBuffer bb = buffers[i];
                bb.clear();
            }

            System.out.println(bytesRead + " " + bytesWritten + " " + messageLength);
        }
    }

    static private final int start = 10;
    static private final int end = 20;

    static public void fileLock(String args[]) throws Exception {
        // Get file channel
        RandomAccessFile raf = new RandomAccessFile("usefilelocks.txt", "rw");
        FileChannel fc = raf.getChannel();

        // Get lock
        System.out.println("trying to get lock");
        FileLock lock = fc.lock(start, end, false);
        System.out.println("got lock!");

        // Pause
        System.out.println("pausing");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
        }

        // Release lock
        System.out.println("going to release lock");
        lock.release();
        System.out.println("released lock");

        raf.close();
    }

    public void charset(String args[]) throws Exception {
        String inputFile = "samplein.txt";
        String outputFile = "sampleout.txt";

        RandomAccessFile inf = new RandomAccessFile(inputFile, "r");
        RandomAccessFile outf = new RandomAccessFile(outputFile, "rw");
        long inputLength = new File(inputFile).length();

        FileChannel inc = inf.getChannel();
        FileChannel outc = outf.getChannel();

        MappedByteBuffer inputData = inc.map(FileChannel.MapMode.READ_ONLY, 0, inputLength);

        Charset latin1 = Charset.forName("ISO-8859-1");
        CharsetDecoder decoder = latin1.newDecoder();
        CharsetEncoder encoder = latin1.newEncoder();

        CharBuffer cb = decoder.decode(inputData);

        // Process char data here

        ByteBuffer outputData = encoder.encode(cb);

        outc.write(outputData);

        inf.close();
        outf.close();
    }
}