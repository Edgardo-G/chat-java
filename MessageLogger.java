import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MessageLogger {
    private static final String FILE = "messages.txt";

    public static void log(String sender, String receiver, String content) throws IOException {
        Message message = new Message(sender, receiver, content);
        Files.writeString(Path.of(FILE), message.format() + "\n",
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static List<String> getHistory(int limit) throws IOException {
        Path path = Path.of(FILE);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(path);
        int from = Math.max(0, lines.size() - limit);
        return lines.subList(from, lines.size());
    }    

    public static List<String> getHistory(int limit, String username) throws IOException {
        Path path = Path.of(FILE);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(path);
        List<String> filtered = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("-> todos") || line.contains(username + " ->") || line.contains("-> " + username)) {
                filtered.add(line);
            }
        }
        int from = Math.max(0, filtered.size() - limit);
        return filtered.subList(from, filtered.size());
    }
}