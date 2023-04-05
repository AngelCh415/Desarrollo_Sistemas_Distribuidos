import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class SecureClienteGET {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String fileName = args[2];
        try {
            // Configuración del SSL context
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance("JKS");
            KeyStore trustStore = KeyStore.getInstance("JKS");

            // Carga del certificado del cliente y del truststore del servidor
            char[] keyStorePassword = "password".toCharArray();
            keyStore.load(new FileInputStream("keystore.jks"), keyStorePassword);
            keyManagerFactory.init(keyStore, keyStorePassword);

            char[] trustStorePasswordArray = "password".toCharArray();
            trustStore.load(new FileInputStream("keystore.jks"), trustStorePasswordArray);
            trustManagerFactory.init(trustStore);

            // Inicialización del SSL context
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            // Creación del socket
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, port);

            // Envío de la petición GET
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(out);
            writer.println("GET " + fileName);
            writer.flush();

            // Recepción de la respuesta del servidor
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String response = reader.readLine();

            if (response.equals("ERROR")) {
                System.out.println("Error: el archivo no se pudo obtener del servidor");
            } else {
                // Obtener la longitud del archivo
                int length = Integer.parseInt(reader.readLine());

                // Crear el archivo y escribir el contenido
                File file = new File(fileName);
                FileOutputStream fileOut = new FileOutputStream(file);
                byte[] buffer = new byte[4096];

                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }
                fileOut.close();

                System.out.println("El archivo " + fileName + " se recibió con éxito.");
            }

            // Cierre del socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
