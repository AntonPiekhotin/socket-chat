package ua.nure.piekhotin.task3;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private String clientUserName;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public static List<ClientHandler> clientHandlers = new ArrayList<>();

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                    true);
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("SERVER: " + clientUserName + " has entered the chat!");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, printWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, printWriter);
                break;
            }
        }
    }

    // Send a message to everyone except me.
    public void broadCastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(this.clientUserName)) {
                    clientHandler.printWriter.println(messageToSend);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadCastMessage("SERVER: " + clientUserName + " has left the chat!");
    }
}
