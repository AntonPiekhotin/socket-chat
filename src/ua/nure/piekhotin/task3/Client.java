package ua.nure.piekhotin.task3;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER = "localhost";
    private static final int PORT = 12345;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private final String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            this.username = username;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void sendMessage() {
        try {
            printWriter.println(username);

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                printWriter.println(username + ": " + messageToSend);
            }
        } catch (Exception e) {
            closeAll(socket, bufferedReader, printWriter);
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, printWriter);
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter) {
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

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for group chat: ");
        String username = scanner.nextLine();

        Socket socket = new Socket(SERVER, PORT);
        Client client = new Client(socket, username);
        client.listenForMessages();
        client.sendMessage();
    }
}
