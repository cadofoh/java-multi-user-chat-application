
package secure_chat_service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.System.err;
import static java.lang.System.exit;


/**
 * This class creates a client that establishes a connection with the server and listens for messages
 */
public class Client {
    // public static final boolean DEBUG = true;
    public static final int TLS_PORT = 43000;
    public static final String TLS_HOST = "localhost";
    public static final String TRUSTSTORE_LOCATION = "CA/ClientKeyStore.jks";
    public static final String TRUSTSTORE_PWD = "password";
    private SSLSocket socket;
    private BufferedReader buffR;
    private BufferedWriter buffW;
    private String username;


    /**
     * Creates an instance of a class
     *
     * @param socket   the socket that provides the connection to the server
     * @param username username of the user
     */
    public Client(SSLSocket socket, String username) {
        try {
            this.socket = socket;
            this.buffW = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.buffR = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException p) {
            releaseResources(socket, buffR, buffW);
        }
    }



    /**
     * This method sends the user messages to the server
     */
    public void sendMessage() {
        try {
            buffW.write(username);
            buffW.newLine();
            buffW.flush();

            var sc = new Scanner(System.in);
            if (socket.isConnected()) {
                do {
                    var messageToSend = sc.nextLine();
                    buffW.write(messageToSend);
                    System.out.println(username + ": " + messageToSend);
                    buffW.newLine();
                    buffW.flush();
                } while (socket.isConnected());
            }
        } catch (IOException p) {
            p.printStackTrace();
            releaseResources(socket, buffR, buffW);
        }
    }

    /**
     * This method listens for messages on the socket
     */
    public void listenForMessage() {
        new Thread(() -> {

            String msgFromChat;

            while (socket.isConnected()) {
                try {
                    msgFromChat = buffR.readLine();
                    Thread.sleep(1500);
                    System.out.println(msgFromChat);
                } catch (IOException | InterruptedException p) {
                    Client.this.releaseResources(socket, buffR, buffW);
                }
            }
        }).start();
    }

    /**
     * @param socket the socket that provides the connection to the server
     * @param buffR  reads data from the socket's input stream
     * @param buffW  writes data to the socket's output stream
     *
     * This method closes all the resources and is called whn asn exception occurs
     */
    public void releaseResources(Socket socket, BufferedReader buffR, BufferedWriter buffW) {
        try {
            if (buffR != null) buffR.close();
            if (buffW != null) buffW.close();
            if (socket != null) socket.close();
        } catch (IOException p) {
            p.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        var correctLogin = false;
        while (!correctLogin) {

            System.out.println("Enter the password to access the chat.");
            var pwD = sc.nextLine();

            if (!pwD.equals("SNS_Chat69")) {
                correctLogin = false;
                System.out.println("Password Incorrect, Kindly try again");
            } else {

                correctLogin = true;
                System.out.println("Enter your chat username");
                var username = sc.nextLine();

                System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_LOCATION);
                System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PWD);

                // if (DEBUG) System.setProperty("javax.net.debug", "all");
                var f = (SSLSocketFactory) SSLSocketFactory.getDefault();
                try (var socket = (SSLSocket) f.createSocket(TLS_HOST, TLS_PORT)) {
                    socket.startHandshake();

                    var client = new Client(socket, username);
                    if (username != null && !username.isEmpty()) {
                        System.out.println("Welcome " + "**" + username + "**" + "\nYou can now send messages");
                    }
                    client.listenForMessage();
                    client.sendMessage();

                } catch (UnknownHostException e) {
                    err.println("Don't know about host " + TLS_HOST);
                    exit(1);

                } catch (IOException e) {
                    err.println("Couldn't get I/O for the connection to " +
                            TLS_HOST);

                }
            }
        }
    }

}
