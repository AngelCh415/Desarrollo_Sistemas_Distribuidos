package Tarea4;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecurePutClient {

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

            // Leer el archivo a enviar
            File file = new File(fileName);
            FileInputStream fileIn = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fileIn.read(fileBytes);
            fileIn.close();

            // Enviar petición PUT al servidor
            writer.write("PUT " + file.getName() + "\n");
            writer.write(file.length() + "\n");
            out.write(fileBytes);
            writer.flush();

            // Leer respuesta del servidor
            String response = reader.readLine();
            if (response.equals("OK")) {
                System.out.println("Archivo enviado con éxito al servidor.");
            } else {
                System.err.println("Error: el servidor no pudo escribir el archivo.");
            }

            // Cerrar la conexión con el servidor
            socket.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
