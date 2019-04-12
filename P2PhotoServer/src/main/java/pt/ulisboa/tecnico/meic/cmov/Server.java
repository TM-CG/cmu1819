package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<User> users;
    private List<Album> albums;
    private List<Pair<String, String>> loggedInUsers;

    /** Socket related **/
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private DataInputStream dis;
    private DataOutputStream dos;

    /** Displays detailed debug for testing**/
    private boolean verboseDebug;

    public static final int SERVER_PORT = 10000;

    public Server() {
        if(!doRead()) {
            this.users = new ArrayList<>();
            this.loggedInUsers = new ArrayList<>();
            this.albums = new ArrayList<>();
        }
        this.verboseDebug = true;
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

    public boolean isVerboseDebugEnabled() {
        return verboseDebug;
    }

    public void setVerboseDebug(boolean verboseDebug) {
        this.verboseDebug = verboseDebug;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Pair<String, String>> getLoggedInUsers() {
        return loggedInUsers;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Checks if a given username exists
     * @param username to be checked
     * @return true if username exists false otherwise
     */
    public boolean usernameExists(String username) {
        boolean result = false;

        synchronized (this.users) {
            for (User user : this.users) {
                if (user.getUsername().equals(username)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Check if a given user (by username) is currently logged in the system.
     * @param username to check
     * @return the sessionID if the user is logged in and null otherwise
     */
    public String usernameIsLoggedOn(String username) {
        String result = null;

        synchronized (this.loggedInUsers) {
            for (Pair<String, String> user: this.loggedInUsers) {
                if (user.getKey().equals(username)) {
                    result = user.getValue();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Given a sessionID returns the corresponding username
     * @param sessionID to be checked
     * @return the username if was a match and null otherwise
     */
    public String getUserNameBySessionID(String sessionID) {
        String result = null;

        synchronized (this.loggedInUsers) {
            for (Pair<String, String> user : this.loggedInUsers) {
                if (user.getValue().equals(sessionID)) {
                    result = user.getKey();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Given a username returns the correspondent User
     * @param username
     * @return the User instance that matches the username
     */
    public User getUserByUsername(String username) {
        User result = null;

        synchronized (this.users) {
            for (User user: users) {
                if (user.getUsername().equals(username)) {
                    result = user;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Given an identifier return the album
     * @param id of the album
     * @return an album object if album exists or null otherwise
     */
    public Album getAlbumById(int id) {
        Album result = null;

        synchronized (this.albums) {
            for (Album album : this.albums) {
                if (album.getID() == id) {
                    result = album;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Given a pattern return all usernames that matches it
     * @param pattern to look for
     * @return a list of all usernames that matches that pattern
     */
    public List<String> findUserNameByPattern(String pattern) {
        List<String> matches = new ArrayList<>();

        synchronized (this.users) {
            for (User user : this.users) {
                if (user.getUsername().matches(pattern))
                    matches.add(user.getUsername());
            }
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

    public String representIntegerList(List<Integer> list) {
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
    public List<Integer> getAlbunsOfGivenUser(String username) {
        List<Integer> albums = new ArrayList<>();

        synchronized (this.albums) {
            for (Album album : this.albums) {
                //User is the owner OR user participates on the album
                if ((album.getOwner().equals(username)) || (album.getIndexOfUser(username) != null))
                    albums.add(album.getID());
            }
        }

        return albums;
    }

    /**
     * Returns a list of albums where the user was invited but not yet accept nor deny the invitation
     * @param username of the user
     * @return a list of albums ids
     */
    public List<Integer> getPendingAlbumsOfGivenUser(String username) {
        List<Integer> albums = new ArrayList<>();

        synchronized (this.albums) {
            for (Album album : this.albums) {
                //User is the owner OR user participates on the album
                if (album.getIndexOfUser(username) == null)
                    albums.add(album.getID());
            }
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
            System.err.println("** SERVER: InitSocket IOException: " + e.getMessage());
        }
    }

    /**
     * Stop any listen on the channel for client's requests.
     */
    public void stopSocket() {
        try {
            if (clientSocket != null)
                clientSocket.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch(IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void addLoggedUser(String username, String sessionID) {
        synchronized (this.loggedInUsers) {
            this.loggedInUsers.add(new Pair<>(username, sessionID));
        }
    }

    public void removeLoggedUser(String username, String sessionID) {
        synchronized (this.loggedInUsers) {
            this.loggedInUsers.remove(new Pair<>(username, sessionID));
        }
    }

    public void addUser(User user) {
        synchronized (this.users) {
            this.users.add(user);
        }
    }

    public void addAlbum(Album album) {
        synchronized (this.albums) {
            this.albums.add(album);
        }
    }

    public int numberOfRegisteredUsers() {
        int size;

        synchronized (this.users){
            size = this.users.size();
        }
        return size;
    }

    public int numberOfRegisteredAlbums() {
        int size;

        synchronized (this.albums){
            size = this.albums.size();
        }
        return size;
    }

    public int numberOfLoggedInUsers() {
        int size;

        synchronized (this.loggedInUsers){
            size = this.loggedInUsers.size();
        }
        return size;
    }

    /**
     * Clears all users and albums: for testing proposes
     */
    public void reset() {
        synchronized (this.albums) {
            this.albums.clear();
            Album.CounterID = 1;
        }
        synchronized (this.loggedInUsers) {
            this.loggedInUsers.clear();
        }
        synchronized (this.users) {
            this.users.clear();
        }
    }

    /**************************************************************/
    /************************Persistence***************************/
    /**************************************************************/

    public void doWrite(){
        System.out.println("Writing UsersAndAlbums ...");

        try {
            File file = new File("UsersAndAlbums.bin");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file, false);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(albums);
            o.writeObject(users);
            o.writeObject(loggedInUsers);
            o.close();
        }
        catch (IOException e) {
            System.out.println("Error initializing stream");
        }
    }


    public boolean doRead() {
        System.out.println("Reading GoodsUser...");
        try {
            File file = new File("UsersAndAlbums.bin");
            if(!file.exists()){
                System.out.println("File UsersAndAlbums.bin does not exists");
                return false;
            }
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi);
            albums = (List<Album>) oi.readObject();
            users = (List<User>) oi.readObject();
            loggedInUsers = (List<Pair<String, String>>) oi.readObject();

            oi.close();
            return true;
        }
        catch (FileNotFoundException e) {
            System.out.println("File UsersAndAlbums.bin not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            e.printStackTrace();
        }
        return false;
    }

}
