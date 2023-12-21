package ua.nure.piekhotin.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket socket = new ServerSocket(12345);

        while (true) {
            Socket s = socket.accept();
            //TODO
        }
    }
}
