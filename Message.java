import java.time.*;
import java.time.format.*;

public class Message {
    private LocalDateTime timestamp;
    private String sender;
    private String receiver;
    private String content;

    public Message(String sender, String receiver, String content) {
        this.timestamp = LocalDateTime.now();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String format() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter) + " | " + sender + " -> " + receiver + " | " + content;
    }
}