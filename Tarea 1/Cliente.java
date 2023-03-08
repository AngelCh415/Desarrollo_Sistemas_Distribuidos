import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Socket servidor = new Socket("localhost", 8080); // direccion y puerto del servidor
        System.out.println("Cliente conectado al servidor B");
        
        BufferedReader entrada = new BufferedReader(new InputStreamReader(servidor.getInputStream()));
        PrintWriter salida = new PrintWriter(new OutputStreamWriter(servidor.getOutputStream()), true);
        int numero,numeroInicial,numeroFinal;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ingrese el numero: ");
        numero = Integer.parseInt(br.readLine());
       /* System.out.println("Ingrese el numero inicial: ");
        numeroInicial = Integer.parseInt(br.readLine());
        System.out.println("Ingrese el numero final: ");
        numeroFinal = Integer.parseInt(br.readLine());*/
        salida.println(numero);
      /*  salida.println(numeroInicial);
        salida.println(numeroFinal);*/
        // recibir respuesta del servidor
        String respuesta = entrada.readLine();
        System.out.println("Respuesta del servidor: " + respuesta);
        servidor.close();
    }
}
