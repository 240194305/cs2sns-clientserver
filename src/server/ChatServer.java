package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public ChatServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port); Socket socket = serverSocket.accept(); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String input;

            while ((input = in.readLine()) != null) {
                System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + input);
                out.println("Echo: " + input);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(1);
        }
    }
}
