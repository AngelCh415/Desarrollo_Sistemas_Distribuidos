import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.security.KeyStore;

public class SecureMultiThreadedServer {

    private final static int PORT = 8000;
    private final static String KEYSTORE_LOCATION = "serverkeystore.jks";
    private final static String KEYSTORE_PASSWORD = "password";
    private final static int THREAD_POOL_SIZE = 10;
    private final static String FILES_DIRECTORY = "./";

    public static void main(String[] args) {
        try {
            SSLServerSocketFactory sslServerSocketfactory = getSSLServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(PORT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                executor.execute(new RequestHandler(sslSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SSLServerSocketFactory getSSLServerSocketFactory() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        FileInputStream fileInputStream = new FileInputStream(KEYSTORE_LOCATION);
        keyStore.load(fileInputStream, KEYSTORE_PASSWORD.toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext.getServerSocketFactory();
    }

    private static class RequestHandler implements Runnable {
        private final SSLSocket sslSocket;

        public RequestHandler(SSLSocket sslSocket) {
            this.sslSocket = sslSocket;
        }

        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
                String request = input.readLine();
                String[] requestParts = request.split(" ");
                String requestType = requestParts[0];
                String fileName = requestParts[1];

                switch (requestType) {
                    case "GET":
                        handleGetRequest(fileName, output);
                        break;
                    case "PUT":
                        int fileSize = Integer.parseInt(requestParts[2]);
                        handlePutRequest(fileName, fileSize, input, output);
                        break;
                    default:
                        output.write("ERROR: Invalid request type");
                        output.newLine();
                        break;
                }
                input.close();
                output.close();
                sslSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleGetRequest(String fileName, BufferedWriter output) throws IOException {
            String filePath = FILES_DIRECTORY + fileName;
            File file = new File(filePath);
            if (file.exists() && !file.isDirectory()) {
                String fileContent = readFileContent(file);
                output.write("OK " + file.length());
                output.newLine();
                output.write(fileContent);
                output.newLine();
            } else {
                output.write("ERROR: File not found");
                output.newLine();
            }
        }

        private void handlePutRequest(String fileName, int fileSize, BufferedReader input, BufferedWriter output) throws IOException {
            String filePath = FILES_DIRECTORY + fileName;
            File file = new File(filePath);
            if (file.exists() && !file.isDirectory()) {
                output.write("ERROR: File already exists");
                output.newLine();
            } else {
                FileWriter fileWriter = new FileWriter(file);
                char[] buffer = new char[1024];
                int bytesRead;
                while (            fileSize > 0 && (bytesRead = input.read(buffer, 0, Math.min(buffer.length, fileSize))) != -1) {
                fileWriter.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }
            fileWriter.close();
            if (fileSize == 0) {
                output.write("OK");
                output.newLine();
            } else {
                file.delete();
                output.write("ERROR: File upload failed");
                output.newLine();
            }
        }
    }

    private String readFileContent(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }
}
}


