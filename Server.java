import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        /* Inicializar el socket del servidor */
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Servidor escuchando en puerto 12345...");

        /*El metodo accept() bloquea la ejecución hasta que un cliente se conecte 
        proba usando nc (netcat) desde la terminal con el puerto 12345 */
        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado!");        

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
        }

        serverSocket.close();
    }
}

