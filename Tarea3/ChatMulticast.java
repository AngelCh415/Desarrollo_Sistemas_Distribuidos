import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatMulticast {
    //Poner chcp 1252 para desplegar los caracteres
    private static final String MULTICAST_IP = "239.0.0.0"; // dirección IP multicast utilizada
    private static final int PORT = 50000; // puerto utilizado para la comunicación multicast
    private static final int BUFFER_SIZE = 1024; // tamaño del buffer para los mensajes

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Debe indicar su nombre de usuario como parámetro.");
            System.exit(1);
        }

        String username = args[0]; // nombre de usuario
        
        // creamos el socket multicast
        MulticastSocket socket = new MulticastSocket(PORT);
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        socket.joinGroup(group);

        // creamos el hilo de recepción de mensajes
        Thread receiverThread = new Thread(() -> {
            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                        socket.receive(packet);
                    String message = new String(packet.getData(), StandardCharsets.UTF_8).trim();
                    // mostramos el mensaje recibido en pantalla
                    System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiverThread.start();

        // leemos los mensajes que escriba el usuario desde la consola
        Scanner scanner = new Scanner(System.in, System.console().charset().name());
        while (true) {
            System.out.print("Escribe tu mensaje: ");
            //Leer el mensaje en UTF-8
            String message = scanner.nextLine();
            // agregamos el nombre de usuario al mensaje
            message = username + ": " + message;
            // creamos el paquete de datos a enviar
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            
        }
    }
}
