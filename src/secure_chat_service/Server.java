
package secure_chat_service;


import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Listens to clients who wish to connect and spawns a new thread when they do
 */
public class Server {
    //public static final boolean DEBUG = true;
    public static final String KEYSTORE_LOCATION = "Keys/ServerKeyStore.jks";
    public static final String KEYSTORE_PASSWORD = "password";
    public static final int TLS_PORT = 43000;
    private final SSLServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = (SSLServerSocket) serverSocket;
    }


    public static void main(String[] args) throws IOException {

        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_LOCATION);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

        //if (DEBUG) System.setProperty("javax.net.debug", "all");
        var ssf = SSLServerSocketFactory.getDefault();
        var serverSocket = (SSLServerSocket) ssf.createServerSocket(TLS_PORT);

        try {
            do {
                serverSocket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
                var socket = serverSocket.accept();
                var server = new Server(serverSocket);

                var clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

                System.out.println("A new user has connected to the server");
            } while (true);
        } catch (IOException p) {
            p.printStackTrace();
        } finally {
            if (serverSocket != null)
                serverSocket.close();
        }
    }


}


