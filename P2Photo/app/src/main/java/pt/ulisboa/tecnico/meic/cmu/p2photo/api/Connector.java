package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import java.util.List;

/*
 * An interface to abstract third-party operations like servers or Wi-Fi direct.
 */
public interface Connector {

    /** Options for List Album **/
    enum ListAlbumOption {
        VIEW_ALL("ALL"),
        VIEW_PAR("PAR"),
        VIEW_OWN("OWN");

        private final String stringValue;
        ListAlbumOption(final String s) { stringValue = s; }
        public String toString() { return stringValue; }
    }

    void logIn(String username, String password) throws P2PhotoException;

    void logOut() throws P2PhotoException;

    void signUp(String username, String password) throws P2PhotoException;

    int createAlbum(String albumDirectoryURL) throws P2PhotoException;

    int createAlbum() throws P2PhotoException;

    int updateAlbum(int albumId, String newUserName) throws P2PhotoException;

    String getAlbumOwner(int albumId) throws P2PhotoException;

    List<Integer> listUserAlbums(ListAlbumOption... options) throws P2PhotoException;

    List<String> findUsers(String searchingPattern) throws P2PhotoException;

    List<String> listUserAlbumSlices(int albumId) throws P2PhotoException;

    List<Integer> listIncomingRequest() throws P2PhotoException;

    void acceptIncomingRequest(int albumId, String cloudDirectoryPath) throws P2PhotoException;

    void rejectIncomingRequest(int albumId) throws P2PhotoException;


    void terminateConn() throws P2PhotoException;

    void toggleDebugMode();

    void reset() throws P2PhotoException;

    String getClientAPIVersion();

    String getServerAPIVersion() throws P2PhotoException;
}