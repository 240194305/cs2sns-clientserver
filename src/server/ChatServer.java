package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private final List<ClientHandlerThread> clients = new CopyOnWriteArrayList<>();

    public ChatServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket inbound = serverSocket.accept();
                ClientHandlerThread client = new ClientHandlerThread(this, inbound);

                client.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(1);
        }
    }

    public void addClient(ClientHandlerThread client) {
        clients.add(client);
    }

    public synchronized void removeClient(ClientHandlerThread client) {
        clients.remove(client);
    }

    public void sendToAll(String line) {
        for (ClientHandlerThread client : clients) {
            client.sendMessage(line);
        }
    }
}
