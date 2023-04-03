package Tarea4;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecureGetClient {

    public static void main(String[] args) {

        String serverHostname = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        try {
            // Establecer la conexión con el servidor
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(serverHostname, serverPort);

            // Obtener flujos de entrada/salida para comunicarse con el servidor
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            // Enviar petición GET al servidor
            writer.write("GET " + fileName + "\n");
            writer.flush();

            // Leer respuesta del servidor
            String response = reader.readLine();
            if (response.equals("OK")) {
                // Leer la longitud del archivo
                String lengthString = reader.readLine();
                long length = Long.parseLong(lengthString);

                // Leer el contenido del archivo
                byte[] fileBytes = new byte[(int) length];
                in.read(fileBytes);

                // Escribir el archivo en disco local
                FileOutputStream fileOut = new FileOutputStream(fileName);
                fileOut.write(fileBytes);
                fileOut.close();

                System.out.println("Archivo recibido con éxito desde el servidor.");
            } else {
                System.err.println("Error: el servidor no pudo leer el archivo.");
            }

            // Cerrar la conexión con el servidor
            socket.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
