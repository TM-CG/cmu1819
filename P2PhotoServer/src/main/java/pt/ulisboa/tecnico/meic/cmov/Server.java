package pt.ulisboa.tecnico.meic.cmov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

    private List<User> users;

    /** Socket related **/
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final int SERVER_PORT = 10000;

    public Server() {
        this.users = new ArrayList<User>();
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public boolean usernameExists(String username) {
        for (User user: users) {
            if (user.getUsername().equals(username))
                return true;
        }
        return false;
    }

    /** === SOCKET RELATED === **/

    public void initSocket() {
        try {
            this.serverSocket = new ServerSocket(SERVER_PORT);
            this.clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(true) {
                String message = in.readLine();

                System.out.println("Client response: " + message);
            }

        } catch(IOException e) {
            System.err.println("IOException!");
        }
    }

    public void stopSocket() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch(IOException e) {
            System.err.println("IOException!");
        }
    }


    private List<String> parseInstruction(String instruction) {
        List<String> args = null;
        if (instruction.startsWith("LOGIN")) {
            args = Arrays.asList(instruction.split(" "));

        }

        return args;
    }


}
