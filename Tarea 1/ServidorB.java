import java.io.*;
import java.net.*;

public class ServidorB {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(8080); // puerto del servidor
        System.out.println("Servidor A en linea");
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
            
            // recibir los 3 numeros del cliente
            int numero = Integer.parseInt(entrada.readLine());
            /*int numeroInicial = Integer.parseInt(entrada.readLine());
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
            */
            //Muestra el numero recibido
            System.out.println("Numero recibido: " + numero);
            salida.println("Vengo del servidor B");
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
