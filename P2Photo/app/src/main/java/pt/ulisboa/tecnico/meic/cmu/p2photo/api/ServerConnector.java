package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.support.v4.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Client API for P2PhotoServer connection
 */
public class ServerConnector {

    //API Instructions (version 0.4)
    private static final String API_LOGIN = "LOGIN %s %s";
    private static final String API_SIGNUP = "SIGNUP %s %s %s";
    private static final String API_LOGOUT = "LOGOUT %s";
    private static final String API_ALB_CR8 = "ALB-CR8 %s \"%s\"";
    private static final String API_ALB_AUP = "ALB-AUP %s %s %s";
    private static final String API_ALB_LST = "ALB-LST %s";
    private static final String API_ALB_UAS = "ALB-UAS %s %s";
    private static final String API_USR_FND = "USR-FND %s %s";
    private static final String API_SHUT = "SHUT";


    //Error messages
    private static final String CONN_PROBLEM = "Problem connecting to server ";
    private static final String CFREQUEST_PROBLEM = "Cannot fullfill request ";
    private static final String RESPONSE_PROBLEM = "Problem decoding server response ";
    private static final String WRONG_ARGS = "Wrong argument or invalid or user is not logged in! ";

    //Server responses must follow API!
    public static final String ERR     = "ERR"  ;
    public static final String NOK_1   = "NOK 1";
    public static final String NOK_2   = "NOK 2";
    public static final String NOK_3   = "NOK 3";
    public static final String NOK_4   = "NOK 4";
    public static final String NOK_5   = "NOK 5";
    public static final String NOK_6   = "NOK 6";
    public static final String NOK_7   = "NOK 7";
    public static final String OK_PLUS = "OK "  ;
    public static final String OK      = "OK"   ;
    public static final String SHUT_OK = "SHUT OK";

    private String serverPath;

    private int serverPort;

    /** Current user session identifier **/
    private String sessionId;

    private Socket socket;

    private PrintWriter out;

    private BufferedReader in;

    private boolean showDebug;

    public ServerConnector(String serverPath, int serverPort) throws P2PhotoException {
        this.serverPath = serverPath;
        this.serverPort = serverPort;
        this.showDebug = false;

        try {
            this.socket = new Socket(serverPath, serverPort);

            this.out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(this.socket.getOutputStream())), true);
            this.in = new BufferedReader(
                    new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            throw new P2PhotoException(CONN_PROBLEM + e.getMessage());
        }
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Authentication of user method
     * @param username the username of the user
     * @param password the password of the user
     * @throws P2PhotoException if login failed or connection with the server is lost
     */
    public void logIn(String username, String password) throws P2PhotoException {
        String request = String.format(API_LOGIN, username, password);

        try {
            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            this.sessionId = response.split(" ")[1];


        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        }
    }

    public void logOut() throws P2PhotoException{
        if (sessionId == null)
            throw new P2PhotoException("User is not logged in!");

        String request = String.format(API_LOGOUT, sessionId);
        this.out.println(request);

        try {
            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            //OK then dispose sessionId
            this.sessionId = null;

        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        }
    }

    /**
     * Registers a new user to the system
     * @param username of the new user
     * @param password of the new user
     * @param cloudURL of the user cloud space provider
     * @throws P2PhotoException
     */
    public void signUp(String username, String password, String cloudURL) throws P2PhotoException {
        try {
            String request = String.format(API_SIGNUP, username, password, cloudURL);

            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);


        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        } catch (NullPointerException e) {
            throw new P2PhotoException(WRONG_ARGS + e.getMessage());
        }
    }

    /**
     * Creates an album on the server
     * @param albumTitle of the album
     * @return an integer which is the albumID
     * @throws P2PhotoException if something wrong happens
     */
    public int createAlbum(String albumTitle) throws P2PhotoException{
        try {
            String request = String.format(API_ALB_CR8, sessionId, albumTitle);

            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            //return albumID
            return Integer.parseInt(response.split(" ")[1]);

        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        } catch (NullPointerException e) {
            throw new P2PhotoException(WRONG_ARGS + e.getMessage());
        }
    }

    /**
     * Updates album user permissions
     * @param albumId of the album to change
     * @param newUserName to be added to the album
     * @return the albumId if successfully add user to album
     * @throws P2PhotoException if something wrong happens
     */
    public int updateAlbum(int albumId, String newUserName) throws P2PhotoException {
        try {
            String request = String.format(API_ALB_AUP, sessionId, albumId, newUserName);

            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            //return albumID
            return Integer.parseInt(response.split(" ")[1]);

        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        } catch (NullPointerException e) {
            throw new P2PhotoException(WRONG_ARGS + e.getMessage());
        }
    }

    /**
     * Given a pattern search for usernames that match that pattern
     * @param searchingPattern the search pattern.
     *                         Could be a simple string or a string with a wildcard at the end
     *                         Or just a wildcard to return all usernames
     * @return arrayList a string array containing all
     * @throws P2PhotoException if something wrong happens
     */
    public List<String> findUsers(String searchingPattern) throws P2PhotoException {
        try {
            String request = String.format(API_USR_FND, sessionId, searchingPattern);

            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            return parseList(response);

        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        } catch (NullPointerException e) {
            throw new P2PhotoException(WRONG_ARGS + e.getMessage());
        }
    }

    /**
     * Returns all albums whose owner is currently logged in and all albums where the user participates
     * @return a list of pairs containing the albumID and the respective title
     * @throws P2PhotoException if something wrong happens
     */
    public List<Pair<Integer, String>> listUserAlbums() throws P2PhotoException {
        try {
            String request = String.format(API_ALB_LST, sessionId);

            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            return parseListPairs(response);

        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        } catch (NullPointerException e) {
            throw new P2PhotoException(WRONG_ARGS + e.getMessage());
        }
    }

    /**
     * Returns a list of all URLS of all participants of a given album
     * @param albumId the album identifier
     * @return a list of URL
     * @throws P2PhotoException
     */
    public List<String> listUserAlbumSlices(int albumId) throws P2PhotoException {
        try {
            String request = String.format(API_ALB_UAS, sessionId, albumId);

            this.out.println(request);

            String response = this.in.readLine();

            if (showDebug) {
                System.out.println("Request: " + request);
                System.out.println("Response: " + response);
            }

            processErrors(response);

            return parseList(response);

        } catch (IOException e) {
            throw new P2PhotoException(CFREQUEST_PROBLEM + e.getMessage());
        } catch (NullPointerException e) {
            throw new P2PhotoException(WRONG_ARGS + e.getMessage());
        }
    }

    /**
     * Closes the connection with the server
     * @throws P2PhotoException if something wrong happens
     */
    public void terminateConn() throws P2PhotoException {
        try {
            this.out.close();
            this.in.close();
            this.socket.close();
        } catch (IOException e) {
            throw new P2PhotoException(CONN_PROBLEM + e.getMessage());
        }
    }

    /**
     * Process error messages from the server
     * @param response
     * @throws P2PhotoException
     */
    private void processErrors(String response) throws P2PhotoException {
        switch (response) {
            case (NOK_1): throw new P2PhotoException(NOK_1);
            case (NOK_2): throw new P2PhotoException(NOK_2);
            case (NOK_3): throw new P2PhotoException(NOK_3);
            case (NOK_4): throw new P2PhotoException(NOK_4);
            case (NOK_5): throw new P2PhotoException(NOK_5);
            case (NOK_6): throw new P2PhotoException(NOK_6);
            case (NOK_7): throw new P2PhotoException(NOK_7);
            case (ERR)  : throw new P2PhotoException(ERR)  ;
        }
    }

    /**
     * Given a string of a response with a list format return the list object
     * @param response of a list to be converted to Java.List
     * @return A List of string with all responses
     */
    private List<String> parseList(String response) {
        //select content to be between < and >
        String content = response.substring(response.indexOf("<") + 1, response.indexOf(">"));
        return Arrays.asList(content.split(" , "));
    }

    /**
     * Given a string of a response with a list format return the list object
     * @param response of a list to be converted to Java.List with pares
     * @return a list of pares for advanced representation objects
     */
    private List<Pair<Integer, String>> parseListPairs(String response) {
        List<Pair<Integer, String>> list = new ArrayList<>();
        //select content to be between < and >
        String content = response.substring(response.indexOf("<") + 1, response.indexOf(">"));

        //In the case of more than one item
        if (content.contains(",")) {

            String[] entries = content.split(" , ");

            for (String rep : entries) {
                System.out.print("entries: " + rep);
                Integer albumId = Integer.parseInt(rep.split(" ")[0]);
                String albumTitle = rep.substring(rep.indexOf("\"") + 1);
                list.add(Pair.create(albumId, albumTitle));
            }
        }
        else if (content.length() > 0) { //when there are exactly one item

            Integer albumId = Integer.parseInt(content.split(" ")[0]);
            String albumTitle = content.substring(content.indexOf("\"") + 1, content.length() - 1);
            list.add(Pair.create(albumId, albumTitle));
        }

        return list;
    }

    /**
     * Display debug messages
     */
    public void toggleDebugMode() {
        this.showDebug = !this.showDebug;
    }


}
