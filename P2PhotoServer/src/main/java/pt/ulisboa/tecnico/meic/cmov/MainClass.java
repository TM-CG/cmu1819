package pt.ulisboa.tecnico.meic.cmov;

public class MainClass {

    private static Server server;

    public static void main(String[] args) {
        server = new Server();

        System.out.println("This is the P2Photo-Server running!");

        server.initSocket();
    }
}
