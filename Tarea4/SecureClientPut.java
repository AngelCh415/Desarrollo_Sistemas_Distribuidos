import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class SecureClientPUT {

    public static void main(String[] args) {

        // Comprobamos que se han proporcionado los tres parámetros requeridos
        if (args.length != 3) {
            System.out.println("Debe proporcionar la dirección IP del servidor, el puerto y el nombre del archivo a enviar");
            return;
        }

        // Recuperamos los parámetros proporcionados en la línea de comandos
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        try {
            // Leemos el archivo del disco local
            File file = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(fileName);
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);
            fileInputStream.close();

            // Creamos un contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

            // Creamos un key manager para que el cliente pueda identificarse ante el servidor
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream("keystore.jks"), "password".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "password".toCharArray());

            // Creamos un trust manager para que el cliente confíe en el servidor
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Configuramos el contexto SSL con los key managers y trust managers creados anteriormente
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Creamos un socket SSL y nos conectamos al servidor
            SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket(serverIP, serverPort);

            // Creamos un lector de mensajes entrantes y un escritor de mensajes salientes
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Enviamos la petición PUT al servidor
            out.println("PUT " + fileName + " " + fileContent.length);
            socket.getOutputStream().write(fileContent);
            socket.getOutputStream().flush();

            // Leemos la respuesta del servidor
            String response = in.readLine();

            // Cerramos los streams y el socket
            in.close();
            out.close();
            socket.close();

            // Si el servidor responde OK, mostramos un mensaje de éxito
            if (response.equals("OK")) {
                System.out.println("El archivo se ha enviado con exito al servidor");
            } else {
                System.out.println("El servidor no ha podido escribir el archivo en el disco local");
            }

        } catch (IOException ex) {
            System.out.println("Ha ocurrido un error de entrada/salida: " + ex.getMessage());
        } catch (Exception ex) {
        System.out.println("Ha ocurrido un error: " + ex.getMessage());
        }
    }
}
