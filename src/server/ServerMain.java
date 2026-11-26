package server;

public class ServerMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("A port must be specified: Usage java ServerMain <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        new ChatServer(port);
    }

}
