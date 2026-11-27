package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandlerThread extends Thread {
    private final Socket socket;
    private final ChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

    public ClientHandlerThread(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;

    }

    @Override
    public void run() {
        super.run();
        boolean hasJoined = false;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            hasJoined = waitForJoinMessage();
            if (!hasJoined) {
                socket.close();
                return;
            }

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MESSAGE ")) {
                    String message = line.substring(8).trim();
                    if (message.isEmpty()) continue;

                    server.sendToAll(name + ": " + message);
                } else if (line.startsWith("QUIT")) {
                    break;
                } else {
                    sendMessage("Error: Invalid command from client");
                }
            }
        } catch (IOException exception) {
            System.err.println("could not handle client request");
        } finally {
            try {
                if (hasJoined) {
                    server.removeClient(this);
                    server.sendToAll(name + " has left the chat.");
                }

                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public synchronized void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private boolean waitForJoinMessage() throws IOException {
        String line = in.readLine();
        if (line != null && line.startsWith("JOIN ")) {
            String name = line.substring(5).trim();
            if (name.length() < 3 || name.length() > 20) {
                out.println("ERROR Name must be between 3 and 20 characters.");
                return false;
            }

            this.name = name;
            server.addClient(this);
            out.println("JOINED as " + name);
            server.sendToAll(name + " has joined the chat.");

            return true;
        }

        return false;
    }
}