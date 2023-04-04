import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class SecureClientPut {
    private final static String TRUSTSTORE_LOCATION = "serverkeystore.jks";
    private final static String TRUSTSTORE_PASSWORD = "password";
    private final static String SERVER_IP = "127.0.0.1";
    private final static int SERVER_PORT = 8000;

    public static void main(String[] args) {
        // Obtener el nombre del archivo a enviar como argumento de línea de comando
        if (args.length < 1) {
            System.out.println("Debe proporcionar el nombre del archivo a enviar");
            return;
        }
        String fileName = args[0];

        try {
            // Configurar el socket SSL del cliente
            System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_LOCATION);
            System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = sslSocketFactory.createSocket(SERVER_IP, SERVER_PORT);

            // Leer el archivo del disco local
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("El archivo " + fileName + " no existe");
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);

            // Enviar la solicitud PUT al servidor
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            printWriter.println("PUT " + fileName);
            printWriter.println(fileContent.length);
            outputStream.write(fileContent);
            outputStream.flush();

            // Leer la respuesta del servidor
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = bufferedReader.readLine();
            if (response.equals("OK")) {
                System.out.println("El archivo " + fileName + " fue enviado con éxito al servidor");
            } else {
                System.out.println("El servidor no pudo escribir el archivo " + fileName + " en el disco local");
            }

            // Cerrar el socket y los flujos de entrada y salida
            fileInputStream.close();
            outputStream.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
