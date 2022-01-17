
package secure_chat_service;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static java.text.MessageFormat.format;

/**
 * This class is responsible for communicating with clients
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedWriter buffW;
    private BufferedReader buffR;
    public static ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    private String username;

    /**
     * @param socket the listening socket
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.buffW = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.buffR = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = buffR.readLine();
            clientHandlerArrayList.add(this);
            relayMessage(format("{0} just joined the chat!.", username));

        } catch (IOException p) {
            releaseResources(socket, buffR, buffW);

        }
    }

    @Override
    public void run() {
        String message;

        if (socket.isConnected()) {
            do {
                try {
                    message = buffR.readLine();
                    relayMessage(format("{0}: {1}", username, message));
                } catch (IOException p) {
                    releaseResources(socket, buffR, buffW);
                    break;
                }
            } while (socket.isConnected());
        }
    }

    /**
     * @param messageToSend the message to be broadcast
     *                      <p>
     *                      This method relays the message to all clients connected to the server
     */
    public void relayMessage(String messageToSend) {
        clientHandlerArrayList.forEach(cH -> {
            try {
                if (!cH.username.equals(username)) {
                    cH.buffW.write(messageToSend);
                    cH.buffW.newLine();
                    cH.buffW.flush();
                }
            } catch (IOException p) {
                releaseResources(socket, buffR, buffW);
            }
        });
    }


    /**
     * This method removes the client after they terminate the connection
     */
    public void removeClient() {
        clientHandlerArrayList.remove(this);
        relayMessage(format("{0} has disconnected from the chat!", username));
    }

    /**
     * @param socket the listening socket
     * @param buffR  reads text from input stream
     * @param buffW  writes text to output stream
     *
     *This method closes the input and output streams of the sockets as well as the socket itself.
     */
    public void releaseResources(Socket socket, BufferedReader buffR, BufferedWriter buffW) {
        removeClient();
        try {
            if (buffR != null) buffR.close();
            if (buffW != null) buffW.close();
            if (socket != null) socket.close();
        } catch (IOException p) {
            p.printStackTrace();

        }
    }


}


