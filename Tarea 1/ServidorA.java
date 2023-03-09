import java.io.*;
import java.net.*;

public class ServidorA {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(8000); // puerto del servidor
        System.out.println("Servidor A en linea");
        while (true) {
            Socket cliente = servidor.accept();
            System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress().getHostAddress());
            new ManejadorCliente(cliente).start(); // crear hilo para el nuevo cliente
        }
    }
}

class ManejadorCliente extends Thread {
    private Socket cliente;
    
    public ManejadorCliente(Socket cliente) {
        this.cliente = cliente;
    }
    
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream()), true);
            
            // recibir los 3 numeros del cliente
            int numero = Integer.parseInt(entrada.readLine());
            int numeroInicial = Integer.parseInt(entrada.readLine());
            int numeroFinal = Integer.parseInt(entrada.readLine());
            
            boolean divide = false;
            for (int i = numeroInicial; i <= numeroFinal; i++) {
                if (numero % i == 0) {
                    divide = true;  
                    break;
                }
            }
            // enviar respuesta al cliente
            if (divide) {
                salida.println("DIVIDE");
            } else {
                salida.println("NO DIVIDE");
            }
            
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
