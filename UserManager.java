import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class UserManager {
    // En este archivo se van a almacenar los usuarios
    private static final String FILE = "users.txt";

    public static String hash(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            //Esto es necesario para poder escribir en el texto y leer
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean authenticate(String username, String password) throws IOException {
        try {
            String hashedPassword = hash(password);
            List<String> lines = Files.readAllLines(Path.of(FILE));
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts[1].equals(hashedPassword)) {
                    return true;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Error al hashear la contraseña", e);
        }
        return false;
    }
}