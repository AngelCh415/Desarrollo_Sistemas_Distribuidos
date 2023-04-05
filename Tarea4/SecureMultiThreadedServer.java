import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class SecureMultiThreadedServer {
    
    private final int port;
    private final SSLServerSocket serverSocket;
    private final ExecutorService threadPool;

    public SecureMultiThreadedServer(int port, String keystoreFile, String keystorePassword) throws Exception {
        this.port = port;
        
        // Configuración de SSL
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

        // Configuración del pool de hilos
        threadPool = Executors.newFixedThreadPool(10);
    }
    
    public void start() {
        System.out.println("Server is running on port " + port);
        while (true) {
            try {
                // Esperar a que llegue una conexión entrante
                Socket socket = serverSocket.accept();
                
                // Crear un nuevo hilo para manejar la conexión
                ConnectionHandler handler = new ConnectionHandler(socket);
                threadPool.submit(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectionHandler implements Runnable {
        
        private final Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Configuración del flujo de entrada
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Leer la petición del cliente
                String request = reader.readLine();

                // Dividir la petición en partes (GET/PUT, nombre del archivo, tamaño del archivo)
                String[] parts = request.split(" ");
                String method = parts[0];
                String filename = parts[1];
                int fileSize = 0;

                if (method.equals("PUT")) {
                    fileSize = Integer.parseInt(parts[2]);
                }

                // Configuración del flujo de salida
                OutputStream out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);

                // Procesar la petición
                if (method.equals("GET")) {
                    // Leer el archivo del disco
                    File file = new File(filename);
                    if (file.exists() && file.isFile()) {
                        FileInputStream fileInputStream = new FileInputStream(file);

                        // Enviar respuesta al cliente
                        writer.println("OK");
                        writer.println(file.length());

                        // Enviar el contenido del archivo al cliente
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }

                        fileInputStream.close();
                    } else {
                        // Enviar respuesta de error al cliente
                        writer.println("ERROR");
                    }
                } else if (method.equals("PUT")) {
                    // Escribir el archivo en el disco
                    FileOutputStream fileOutputStream = new FileOutputStream(filename);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    int totalBytesRead = 0;
                    while (totalBytesRead < fileSize && (bytesRead = socket.getInputStream().read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                    }
    
                    fileOutputStream.close();
    
                    // Verificar que se haya escrito correctamente
                    File file = new File(filename);
                    if (file.exists() && file.isFile() && file.length() == fileSize) {
                        // Enviar respuesta al cliente
                        writer.println("OK");
                    } else {
                        // Enviar respuesta de error al cliente
                        writer.println("ERROR");
                    }
                } else {
                    // Enviar respuesta de error al cliente si la petición no es GET o PUT
                    writer.println("ERROR");
                }
    
                // Cerrar la conexión
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            SecureMultiThreadedServer server = new SecureMultiThreadedServer(8000, "keystore.jks", "password");
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
