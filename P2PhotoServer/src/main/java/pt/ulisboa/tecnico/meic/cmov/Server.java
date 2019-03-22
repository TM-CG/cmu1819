package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private List<User> users;
    private List<Album> albums;
    private List<Pair<String, String>> loggedInUsers;

    /** Socket related **/
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private DataInputStream dis;
    private DataOutputStream dos;

    private static final int SERVER_PORT = 10000;

    public Server() {
        this.users = new ArrayList<>();
        this.loggedInUsers = new ArrayList<>();
        this.albums = new ArrayList<>();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    /**
     * Checks if a given username exists
     * @param username to be checked
     * @return true if username exists false otherwise
     */
    public boolean usernameExists(String username) {
        for (User user: users) {
            if (user.getUsername().equals(username))
                return true;
        }
        return false;
    }

    /**
     * Check if a given user (by username) is currently logged in the system.
     * @param username to check
     * @return the sessionID if the user is logged in and null otherwise
     */
    public String usernameIsLoggedOn(String username) {
        for (Pair<String, String> user: this.loggedInUsers) {
            if (user.getKey().equals(username))
                return user.getValue();
        }
        return null;
    }

    /**
     * Given a sessionID returns the corresponding username
     * @param sessionID to be checked
     * @return the username if was a match and null otherwise
     */
    public String getUserNameBySessionID(String sessionID) {
        for (Pair<String, String> user: this.loggedInUsers) {
            if (user.getValue().equals(sessionID))
                return user.getKey();
        }
        return null;
    }

    /**
     * Given a username returns the correspondent User
     * @param username
     * @return the User instance that matches the username
     */
    public User getUserByUsername(String username) {
        for (User user: users) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    /**
     * Given a pattern return all usernames that matches it
     * @param pattern to look for
     * @return a list of all usernames that matches that pattern
     */
    public List<String> findUserNameByPattern(String pattern) {
        List<String> matches = new ArrayList<>();

        for (User user: users) {
            if (user.getUsername().matches(pattern))
                matches.add(user.getUsername());
        }

        return matches;
    }

    /**
     * Given a list of something represent all items as a string of type <user1, user2 , ... , userN>
     * @param list the list to be represented
     * @return a string with matches representation
     */
    public String representList(List<String> list) {
        String rep = "<";
        int len = list.size();

        for (int i = 0; i < len; i++) {
            rep += list.get(i);

            if (i != (len - 1))
                rep += " , ";
        }

        rep += ">";
        return rep;
    }

    /**
     * Given a username returns a list with all albums ID where the user participates or owns
     * @param username of the user to search
     * @return a list of all albums ID
     */
    public List<String> getAlbunsOfGivenUser(String username) {
        List<String> albums = new ArrayList<>();

        for (Album album : this.albums) {
            //User is the owner OR user participates on the album
            if ((album.getOwner().getUsername().equals(username)) || (album.getIndexOfUser(username) != null))
                //vitor: bah!!
                albums.add(new Integer(album.getID()).toString());
        }

        return albums;
    }


    /** ======================================= SOCKET RELATED ======================================= **/

    /**
     * Function to start receiving request from the clients.
     */
    public void initSocket() {

        try {
            this.serverSocket = new ServerSocket(SERVER_PORT);

            while (true) {
                //When receiving a connection from a user starts processing in a new thread!
                this.clientSocket = serverSocket.accept();

                dis = new DataInputStream(this.clientSocket.getInputStream());
                dos = new DataOutputStream(this.clientSocket.getOutputStream());

                Thread thread = new Worker(this);
                thread.start();

            }

        }catch (IOException e) {
            System.err.println("** SERVER: InitSocket IOException!");
        }
    }

    /**
     * Stop any listen on the channel for client's requests.
     */
    public void stopSocket() {
        try {
            dis.close();
            dos.close();
            clientSocket.close();
            serverSocket.close();
        } catch(IOException e) {
            System.err.println("IOException!");
        }
    }

    public void addLoggedUser(String username, String sessionID) {
        synchronized (this) {
            this.loggedInUsers.add(new Pair<>(username, sessionID));
        }
    }

    public void removeLoggedUser(String username, String sessionID) {
        synchronized (this) {
            this.loggedInUsers.remove(new Pair<>(username, sessionID));
        }
    }

    public void addUser(User user) {
        synchronized (this) {
            this.users.add(user);
        }
    }

    public void addAlbum(Album album) {
        synchronized (this) {
            this.albums.add(album);
        }
    }

}
