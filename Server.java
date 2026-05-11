import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    // En la lista habra solo objetos PrintWriter, que son los objetos que se encargan de enviar mensajes a los clientes    
    // CopyOnWriteArrayList implementa la interfaz List y permite modificar la lista mientras se itera sobre ella mostrando una copia mientras se modifica
    static List<PrintWriter> clients = new CopyOnWriteArrayList<>();
    static Map<PrintWriter, String> names = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        /* Inicializar el socket del servidor */
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Servidor escuchando en puerto 12345...");

        while (true) {
            /*El metodo accept() bloquea la ejecución hasta que un cliente se conecte 
            proba usando nc (netcat) desde la terminal con el puerto 12345 */
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado!");   

            // Arma una lista con todos los clientes que se van conectando
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            clients.add(output);
            
            //Luego de crear el hilo el proceso principal sigue hasta volver a serverSocket.accept()
            Thread thread = new Thread(() -> {
                String name = null;
                try{            
                    /* BufferedReader funciona como un buffer que almacena el texto que manda el cliente */
                    /* InputSteamReader recibe el stream de bytes y los convierte en caracteres */
                    BufferedReader input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                    );

                    output.println("Usuario:");
                    String username = input.readLine();
                    output.println("Contraseña:");
                    String password = input.readLine();

                    if (!UserManager.authenticate(username, password)) {
                        output.println("*** Credenciales incorrectas. Cerrando conexión. ***");
                        clientSocket.close();
                        return;
                    }
                    
                    name = username;
                    names.put(output, name);
                    System.out.println(name + " se conectó.");
                    for (PrintWriter client : clients) {
                            if (client != output) {
                                client.println("*** " + name + " se unió al chat ***");
                            }
                    }

                    /* Muestra los mensajes linea por linea hasta que reciba nulo, que es lo que recibe el 
                    socket cuando cierro la conexion */
                    String message;
                    while ((message = input.readLine()) != null) {
                        if (message.equals("/list")) {
                            output.println("*** Usuarios conectados: " + String.join(", ", names.values()) + " ***");
                            continue;
                        }
                        if (message.startsWith("/msg ")) {
                            String[] parts = message.split(" ", 3);
                            if (parts.length < 3) {
                                output.println("*** Uso: /msg <usuario> <mensaje> ***");
                                continue;
                            }
                            String targetName = parts[1];
                            String privateMessage = parts[2];
                            boolean found = false;
                            for (Map.Entry<PrintWriter, String> entry : names.entrySet()) {
                                if (entry.getValue().equals(targetName)) {
                                    entry.getKey().println("[privado de " + name + "]: " + privateMessage);
                                    output.println("[privado a " + targetName + "]: " + privateMessage);
                                    MessageLogger.log(name, targetName, privateMessage);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                output.println("*** Usuario " + targetName + " no encontrado ***");
                            }
                            continue;
                        }
                        if (message.equals("/history")) {
                            List<String> history = MessageLogger.getHistory(20, name);
                            if (history.isEmpty()) {
                                output.println("*** No hay mensajes en el historial ***");
                            } else {
                                output.println("*** Últimos mensajes ***");
                                for (String line : history) {
                                    output.println(line);
                                }
                                output.println("*** Fin del historial ***");
                            }
                            continue;
                        }
                        MessageLogger.log(name, "todos", message);
                        // Si client y output son lo mismo significa que es el mismo mensaje que el cliente acaba de enviar, por lo que no se lo reenvia a ese cliente sino a los demas clientes conectados
                        // A diferencia de python la variable auxiiar aca (client) se debe declarar
                        System.out.println(message);
                        for (PrintWriter client : clients) {
                            if (client != output) {
                                client.println(message);
                            }
                        }
                    }
                // Error de conexion
                } catch (IOException e) {
                    System.out.println("Cliente desconectado.");
                // El cliente finalizo la conexion
                } finally { 
                    clients.remove(output);
                    names.remove(output); 
                    if (name != null){
                        for (PrintWriter client : clients){
                            System.out.println("*** " + name + " abandonó el chat ***");                       
                        }
                    } 
                    output.close();  
                }           
            });
            thread.start();
        }
    }
}

