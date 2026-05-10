import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        // El parametro true en el constructor de PrintWriter hace que se vacie el buffer cada vez que se llama a println, lo que es necesario para que los mensajes se envien inmediatamente
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        // Necesitamos dos bufferedReader, uno para leer los mensajes del servidor y otro para leer lo que el usuario escribe por teclado
        BufferedReader input = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );
        BufferedReader keyboard = new BufferedReader(
            new InputStreamReader(System.in)
        );

        String name = null;
        String serverMessage;
        while ((serverMessage = input.readLine()) != null) {
            System.out.println(serverMessage);
            if (serverMessage.equals("Usuario:")) {
                String username = keyboard.readLine();
                output.println(username);
                name = username;
            } else if (serverMessage.equals("Contraseña:")) {
                String password = keyboard.readLine();
                output.println(password);
            } else {
                break;
            }
        }

        if (name == null) {
            socket.close();
            return;
        }

        final String finalName = name;
        
        // Este hilo escucha los mensajes mientras el hilo principal se encarga de enviar los mensajes que el usuario escribe por teclado
        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println("Mensaje: " + message);
                }
            } catch (IOException e) {
                System.out.println("Conexión perdida.");
            }
            // cuando el try termina es porque el servidor cerro la conexion, sin el finally el cliente se quedaria esperando mensajes que nunca van a llegar
            finally {
                System.out.println("Desconectado del servidor.");
                System.exit(0);
            }
        });
        // Si el hilo no es daemon, el programa no termina aunque el servidor cierre la conexion, el hilo que escucha mensajes queda colgado y deja el programa ejecutandose al pedo
        receiveThread.setDaemon(true);
        receiveThread.start();

        String message;
        while ((message = keyboard.readLine()) != null) {
            if (message.isEmpty()) continue;
            if (message.startsWith("/")) {
                output.println(message);
            } else {
                output.println(finalName + ": " + message);
            }
        }

        socket.close();
    }
}