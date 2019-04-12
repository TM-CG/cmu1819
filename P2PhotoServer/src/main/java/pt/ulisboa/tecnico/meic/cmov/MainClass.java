package pt.ulisboa.tecnico.meic.cmov;

import java.io.IOException;

public class MainClass {

    private static Server server;

    public static void main(String[] args) {
        server = new Server();

        System.out.println("This is the P2Photo-Server running!");

        Thread mainThread = new Thread(() -> server.initSocket());

        mainThread.start();

        try {
            System.out.println("Press ENTER to close server ...");
            System.in.read();
        } catch (IOException e) {
            System.out.println("** MainClass IOException: " + e.getMessage());
        }

        System.exit(0);
    }
}
