package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * A Class for describing the Worker of the Server in order to respond to clients requests.
 */
public class Worker extends Thread {
    private PrintWriter out;
    private BufferedReader in;
    final Socket s;
    private Server server;

    public Worker(Server server) {
        this.s = server.getClientSocket();
        try {
            this.out = new PrintWriter(this.s.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
        } catch (IOException e) {
            System.err.println("** WORKER: Constructor Streams error");
        }
        this.server = server;
    }

    @Override
    public void run() {
        String message;
        List<String> args;

        while(true) {
            try {
                message = in.readLine();

                //parse the instruction that came from the client
                args = parseInstruction(message);

                //sends response back to client
                out.println(processInstruction(args));

            } catch (IOException e) {
                System.err.println("** WORKER: IOException when Worker is running!");
            }
        }

    }

    /**
     * Given an instruction (String) parses it according to the several criteria.
     * @param instruction
     * @return A list of string with the arguments
     */
    private List<String> parseInstruction(String instruction) {
        List<String> args = null;
        if (instruction.startsWith("LOGIN") || instruction.startsWith("SIGNUP") || (instruction.startsWith("LOGOUT")) || (instruction.startsWith("ALB-AUP") || (instruction.startsWith("USR-FND")))) {
            args = Arrays.asList(instruction.split(" "));
        } else if (instruction.startsWith("ALB")) {
            //Split by space ignoring spaces inside quotes
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(instruction);

            args = new ArrayList<>();
            while (m.find())
                args.add(m.group(1).replace("\"", ""));
        }
        return args;
    }

    /**
     * Given a list of arguments and a function execute it
     * @param args list of arguments
     * @return the output of the execution
     */
    private String processInstruction(List<String> args) {

        try {
            String instruction = args.get(0);
            String username;
            String password;
            String sessionId;
            String albumTitle;
            String pattern;
            User user;

            switch (instruction) {
                case "LOGIN":
                    username = args.get(1);
                    password = args.get(2);

                    if (!server.usernameExists(username)) {
                        System.out.println("** LOGIN: User " + username + " does not exists");
                        return "NOK 1";
                    } else {
                        user = server.getUserByUsername(username);
                        if (!user.getPassword().equals(password)) {
                            System.out.println("** LOGIN: User " + username + " failed!");
                            return "NOK 2";
                        } else {

                            sessionId = server.usernameIsLoggedOn(username);

                            if (sessionId == null) {
                                sessionId = Long.toHexString(Double.doubleToLongBits(Math.random()));
                                server.addLoggedUser(username, sessionId);
                            }
                            return "OK " + sessionId;


                        }

                    }

                case "SIGNUP":
                    username = args.get(1);
                    password = args.get(2);


                    //Check if user exists
                    if (server.usernameExists(username)) {
                        System.out.println("** SIGNUP: User " + username + " already exists");
                        return "NOK 3";
                    } else {
                        //Adds user
                        server.addUser(new User(username, password));
                        System.out.println("** SIGNUP: Successfully added user " + username);
                        return "OK";
                    }

                case "LOGOUT":
                    sessionId = args.get(1);
                    username = server.getUserNameBySessionID(sessionId);

                    if (username == null) {
                        System.out.println("** LOGOUT: Invalid sessionID!");
                        return "NOK 4";
                    } else {
                        server.removeLoggedUser(username, sessionId);
                        return "OK";
                    }

                    // === ALBUM RELATED OPERATIONS ===
                case "ALB-CR8":
                    sessionId = args.get(1);
                    albumTitle = args.get(2);
                    User owner = server.getUserByUsername(server.getUserNameBySessionID(sessionId));

                    if (owner == null) {
                        System.out.println("** ALB-CR8: Invalid sessionID!");
                        return "NOK 4";
                    }
                    else {

                        server.addAlbum(new Album(Album.CounterID, albumTitle, owner));

                        System.out.println("** ALB-CR8: User " + owner.getUsername() + " just created one album with title: '" + albumTitle + "'");
                        return "OK " + Album.CounterID++;
                    }

                    // === FIND USERS ===
                case "USR-FND":
                    sessionId = args.get(1);
                    pattern = args.get(2);
                    List<String> matches;

                    if (server.getUserNameBySessionID(sessionId) == null) {
                        System.out.println("** USR-FND: Invalid sessionID!");
                        return "NOK 4";
                    }
                    else {

                        if (pattern.contains("*")) {
                            matches = server.findUserNameByPattern("\\b(\\w*" + pattern.replace("*", "") + "\\w*)\\b");
                        } else matches = server.findUserNameByPattern("\\b(\\w*" + pattern + "\\w*)\\b");

                        return "OK " + server.representList(matches);
                    }

                case "ALB-LST":
                    sessionId = args.get(1);

                    if (server.getUserNameBySessionID(sessionId) == null) {
                        System.out.println("** ALB-LST: Invalid sessionID!");
                        return "NOK 4";
                    } else {
                        username = server.getUserNameBySessionID(sessionId);
                        List<String> albums = server.getAlbunsOfGivenUser(username);

                        return "OK " + server.representList(albums);

                    }



            }
        } catch(Exception e) {
            return "ERR";
        }

        return null;
    }
}
