package Tarea4;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecureMultiThreadedServer {
    private SSLServerSocket serverSocket;

    public SecureMultiThreadedServer(int port) throws IOException {
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        serverSocket = (SSLServerSocket) factory.createServerSocket(port);
        serverSocket.setEnabledCipherSuites(serverSocket.getSupportedCipherSuites());
    }

    public void start() {
        while (true) {
            try {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final SSLSocket clientSocket;

        public ClientHandler(SSLSocket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String request = in.readLine();
                String[] parts = request.split(" ");
                String method = parts[0];
                String fileName = parts[1];

                if (method.equals("GET")) {
                    handleGetRequest(fileName, out);
                } else if (method.equals("PUT")) {
                    handlePutRequest(fileName, in, out);
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleGetRequest(String fileName, BufferedWriter out) throws IOException {
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                byte[] buffer = new byte[(int) file.length()];
                bufferedInputStream.read(buffer, 0, buffer.length);

                out.write("OK " + buffer.length + "\n");
                out.flush();

                clientSocket.getOutputStream().write(buffer, 0, buffer.length);
                clientSocket.getOutputStream().flush();
            } else {
                out.write("ERROR\n");
                out.flush();
            }
        }

        private void handlePutRequest(String fileName, BufferedReader in, BufferedWriter out) throws IOException {
            int fileSize = Integer.parseInt(in.readLine());
            char[] buffer = new char[fileSize];

            if (in.read(buffer, 0, fileSize) != -1) {
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(new String(buffer).getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

                out.write("OK\n");
                out.flush();
            } else {
                out.write("ERROR\n");
                out.flush();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        SecureMultiThreadedServer server = new SecureMultiThreadedServer(8000);
        server.start();
    }
}
