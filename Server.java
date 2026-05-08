import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    // En la lista habra solo objetos PrintWriter, que son los objetos que se encargan de enviar mensajes a los clientes    
    static List<PrintWriter> clients = new ArrayList<>();
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
                try{            
                    /* BufferedReader funciona como un buffer que almacena el texto que manda el cliente */
                    /* InputSteamReader recibe el stream de bytes y los convierte en caracteres */
                    BufferedReader input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                    );

                    /* Muestra los mensajes linea por linea hasta que reciba nulo, que es lo que recibe el 
                    socket cuando cierro la conexion */
                    String message;
                    while ((message = input.readLine()) != null) {
                        System.out.println("Mensaje recibido: " + message);
                        // Si client y output son lo mismo significa que es el mismo mensaje que el cliente acaba de enviar, por lo que no se lo reenvia a ese cliente sino a los demas clientes conectados
                        // A diferencia de python la variable auxiiar aca (client) se debe declarar
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
                    System.out.println("Cliente desconectado.");
                    output.close();
                    clients.remove(output);
                }           
            });
            thread.start();
        }
    }
}

