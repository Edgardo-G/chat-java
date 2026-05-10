import java.io.*;
import java.nio.file.*;

public class AddUser {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Uso: java AddUser <usuario> <contraseña>");
            return;
        }
        String username = args[0];
        String hashedPassword = UserManager.hash(args[1]);
        String line = username + ":" + hashedPassword;
        // Los parametros de StandardOption dicen que hacer si el archivo no existe y que hacer si si existe
        Files.writeString(Path.of("users.txt"), line + "\n",
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println("Usuario " + username + " agregado.");
    }
}