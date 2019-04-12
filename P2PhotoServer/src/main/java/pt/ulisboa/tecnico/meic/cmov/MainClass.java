package pt.ulisboa.tecnico.meic.cmov;

import java.io.IOException;
import java.net.InetAddress;

public class MainClass {

    private static Server server;

    public static void main(String[] args) {
        server = new Server();

        Thread mainThread = new Thread(() -> server.initSocket());

        mainThread.start();

        try {
            InetAddress IP=InetAddress.getLocalHost();
            System.out.println("This is the P2Photo-Server running at " + IP.getHostAddress() + " port " + Server.SERVER_PORT);
            System.out.println("Press ENTER to close server ...");
            System.in.read();
            server.stopSocket();
        } catch (IOException e) {
            System.out.println("** MainClass IOException: " + e.getMessage());
        }

        System.exit(0);
    }
}
