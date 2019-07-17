import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;

public class NIO2Sample {
    public static void randomAccessFileRead(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        // read a file using SeekableByteChannel
        try (SeekableByteChannel seekableByteChannel = Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.READ))) {
            ByteBuffer buffer = ByteBuffer.allocate(12);
            String encoding = System.getProperty("file.encoding");
            buffer.clear();
            while (seekableByteChannel.read(buffer) > 0) {
                buffer.flip();
                System.out.print(Charset.forName(encoding).decode(buffer));
                buffer.clear();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void randomAccessFileWrite(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        // write a file using SeekableByteChannel
        try (SeekableByteChannel seekableByteChannel = Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            ByteBuffer buffer = ByteBuffer.wrap(
                    "Rafa Nadal produced another masterclass of clay-court tennis to win his fifth French Open title ..."
                            .getBytes());
            int write = seekableByteChannel.write(buffer);
            System.out.println("Number of written bytes: " + write);
            buffer.clear();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void seekableByteChannelWithFileAttribute(String[] args) {
        Path path = Paths.get("home/rafaelnadal/email", "email.txt");
        ByteBuffer buffer = ByteBuffer
                .wrap("Hi Rafa, I want to congratulate you for the amazing match that you played ... ".getBytes());
        // create the custom permissions attribute for the email.txt file
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r------");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        // write a file using SeekableByteChannel
        try (SeekableByteChannel seekableByteChannel = Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND), attr)) {
            int write = seekableByteChannel.write(buffer);
            System.out.println("Number of written bytes: " + write);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        buffer.clear();
    }

    public static void oldReadableByteChannel(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        // read a file using ReadableByteChannel
        try (ReadableByteChannel readableByteChannel = Files.newByteChannel(path)) {
            ByteBuffer buffer = ByteBuffer.allocate(12);
            buffer.clear();
            String encoding = System.getProperty("file.encoding");
            while (readableByteChannel.read(buffer) > 0) {
                buffer.flip();
                System.out.print(Charset.forName(encoding).decode(buffer));
                buffer.clear();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void oldWritableByteChannel(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/grandslam/RolandGarros", "story.txt");
        // write a file using WritableByteChannel
        try (WritableByteChannel writableByteChannel = Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.APPEND))) {
            ByteBuffer buffer = ByteBuffer.wrap("Vamos Rafa!".getBytes());
            int write = writableByteChannel.write(buffer);
            System.out.println("Number of written bytes: " + write);
            buffer.clear();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void playWithPositionRead(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "MovistarOpen.txt");
        ByteBuffer buffer = ByteBuffer.allocate(1);
        String encoding = System.getProperty("file.encoding");
        try (SeekableByteChannel seekableByteChannel = (Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.READ)))) {
            // the initial position should be 0 anyway
            seekableByteChannel.position(0);
            System.out.println("Reading one character from position: " + seekableByteChannel.position());
            seekableByteChannel.read(buffer);
            buffer.flip();
            System.out.print(Charset.forName(encoding).decode(buffer));
            buffer.rewind();
            // get into the middle
            seekableByteChannel.position(seekableByteChannel.size() / 2);
            System.out.println("\nReading one character from position: " + seekableByteChannel.position());
            seekableByteChannel.read(buffer);
            buffer.flip();
            System.out.print(Charset.forName(encoding).decode(buffer));
            buffer.rewind();
            // get to the end
            seekableByteChannel.position(seekableByteChannel.size() - 1);
            System.out.println("\nReading one character from position: " + seekableByteChannel.position());
            seekableByteChannel.read(buffer);
            buffer.flip();
            System.out.print(Charset.forName(encoding).decode(buffer));
            buffer.clear();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void playWithPositionWrite(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "MovistarOpen.txt");
        ByteBuffer buffer_1 = ByteBuffer.wrap(
                "Great players participate in our tournament, like: Tommy Robredo, Fernando Gonzalez, Jose Acasuso or Thomaz Bellucci."
                        .getBytes());
        ByteBuffer buffer_2 = ByteBuffer.wrap("Gonzalez".getBytes());
        try (SeekableByteChannel seekableByteChannel = (Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.WRITE)))) {
            // append some text at the end
            seekableByteChannel.position(seekableByteChannel.size());
            while (buffer_1.hasRemaining()) {
                seekableByteChannel.write(buffer_1);
            }
            // replace "Gonsales" with "Gonzalez"
            seekableByteChannel.position(301);
            while (buffer_2.hasRemaining()) {
                seekableByteChannel.write(buffer_2);
            }
            buffer_1.clear();
            buffer_2.clear();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void truncate(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BrasilOpen.txt");
        ByteBuffer buffer = ByteBuffer.wrap(("The tournament has taken a lead in environmental"
                + "conservation efforts, with highlights including the planting of 500 trees to neutralise carbon"
                + "emissions and providing recyclable materials to local children for use in craft work.").getBytes());
        try (SeekableByteChannel seekableByteChannel = (Files.newByteChannel(path,
                EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE)))) {
            seekableByteChannel.truncate(200);
            seekableByteChannel.position(seekableByteChannel.size() - 1);
            while (buffer.hasRemaining()) {
                seekableByteChannel.write(buffer);
            }
            buffer.clear();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void fileChannelMappedMemoryFile(String[] args) {
        Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BrasilOpen.txt");
        MappedByteBuffer buffer = null;
        try (FileChannel fileChannel = (FileChannel.open(path, EnumSet.of(StandardOpenOption.READ)))) {
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        } catch (IOException ex) {
            System.err.println(ex);
        }
        if (buffer != null) {
            try {
                Charset charset = Charset.defaultCharset();
                CharsetDecoder decoder = charset.newDecoder();
                CharBuffer charBuffer = decoder.decode(buffer);
                String content = charBuffer.toString();
                System.out.println(content);
                buffer.clear();
            } catch (CharacterCodingException ex) {
                System.err.println(ex);
            }
        }
    }
}