import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.nio.charset.Charset;

public class ChatMulticast {
    
    public static void main(String[] args) throws Exception {
        
        // Verificar que se proporcionó un nombre de usuario como argumento
        if (args.length != 1) {
            System.out.println("Debe proporcionar el nombre de usuario como argumento");
            System.exit(1);
        }
        String username = args[0];
        
        // Crear el socket multicast y unirse al grupo
        InetAddress group = InetAddress.getByName("239.0.0.0");
        MulticastSocket socket = new MulticastSocket(50000);
        socket.joinGroup(group);
        
        // Crear un thread para recibir los mensajes del resto de los nodos
        Thread receiver = new Thread(() -> {
            try {
                byte[] buffer = new byte[100];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), Charset.forName("UTF-8")).trim();
                    String[] parts = received.split("--->");
                    String sender = parts[0];
                    String message = parts[1];
                    System.out.println("[" + sender + "]: " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiver.start();
        
        // Ciclo infinito para enviar mensajes desde este nodo
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Escribe tu mensaje: ");
            String message = scanner.nextLine().trim();
            String data = username + "--->" + message;
            byte[] buffer = data.getBytes(Charset.forName("UTF-8"));
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 50000);
            socket.send(packet);
        }
        
    }
    
}
