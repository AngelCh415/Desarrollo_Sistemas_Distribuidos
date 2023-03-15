import java.io.*;
import java.net.*;

public class ServidorB {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(8081); // puerto del servidor
        System.out.println("Servidor B en linea");
        while (true) {
            Socket cliente = servidor.accept();
            System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress().getHostAddress());
            new CadenaCliente(cliente).start(); // crear hilo para el nuevo cliente
        }
    }
}

class CadenaCliente extends Thread {
    private Socket cliente;
    
    public CadenaCliente(Socket cliente) {
        this.cliente = cliente;
    }
    
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream()), true);
            long numero = Long.parseLong(entrada.readLine());
            long k = numero/3;
            long inicio2= k+1;
            long k2 = k*2;
            long inicio3 = k2+1;
            long fin = numero-1;
            //Hacemos tres conexiones al servidorA en diferentes ventanas
            Socket servidorA = new Socket("localhost", 8000); // direccion y puerto del servidor
            System.out.println("ServidorB conectado al servidor A");
            BufferedReader entradaA = new BufferedReader(new InputStreamReader(servidorA.getInputStream()));
            PrintWriter salidaA = new PrintWriter(new OutputStreamWriter(servidorA.getOutputStream()), true);
            salidaA.println(numero);
            salidaA.println(2);
            salidaA.println(k);
            String respuestaA = entradaA.readLine();
            servidorA.close();
            Socket servidorA2 = new Socket("localhost", 8000); // direccion y puerto del servidor
            System.out.println("ServidorB conectado al servidor A");
            BufferedReader entradaA2 = new BufferedReader(new InputStreamReader(servidorA2.getInputStream()));
            PrintWriter salidaA2 = new PrintWriter(new OutputStreamWriter(servidorA2.getOutputStream()), true);
            salidaA2.println(numero);
            salidaA2.println(inicio2);
            salidaA2.println(k2);
            String respuestaA2 = entradaA2.readLine();
            servidorA2.close();
            Socket servidorA3 = new Socket("localhost", 8000); // direccion y puerto del servidor
            System.out.println("ServidorB conectado al servidor A");
            BufferedReader entradaA3 = new BufferedReader(new InputStreamReader(servidorA3.getInputStream()));
            PrintWriter salidaA3 = new PrintWriter(new OutputStreamWriter(servidorA3.getOutputStream()), true);
            salidaA3.println(numero);
            salidaA3.println(inicio3);
            salidaA3.println(fin);
            String respuestaA3 = entradaA3.readLine();
            servidorA3.close();
            boolean iguales = false;
            //Hace 3 conexiones con el servidor A
            // enviar respuesta al cliente
            if (respuestaA.equals("NO DIVIDE") && respuestaA2.equals("NO DIVIDE") && respuestaA3.equals("NO DIVIDE")) {
                iguales = true;
            }
            if (iguales) {
                salida.println("ES PRIMO");
            } else {
                salida.println("NO ES PRIMO");
            }
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
