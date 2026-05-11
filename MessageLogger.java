import java.io.*;
import java.nio.file.*;

public class MessageLogger {
    private static final String FILE = "messages.txt";

    public static void log(String sender, String receiver, String content) throws IOException {
        Message message = new Message(sender, receiver, content);
        Files.writeString(Path.of(FILE), message.format() + "\n",
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}