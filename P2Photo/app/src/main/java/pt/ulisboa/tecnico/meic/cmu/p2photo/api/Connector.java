package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

/*
 * An interface to abstract third-party operations like servers or Wi-Fi direct.
 */
public interface Connector {

    public void logIn(String username, String password) throws P2PhotoException;

    public void logOut() throws P2PhotoException;

    public void signUp(String username, String password) throws P2PhotoException;

    public int createAlbum(String albumDirectoryURL) throws P2PhotoException;

    public int createAlbum() throws P2PhotoException;

    public int updateAlbum(int albumId, String newUserName) throws P2PhotoException;

    public String getAlbumOwner(int albumId) throws P2PhotoException;

    public List<Integer> listUserAlbums(ListAlbumOption... options) throws P2PhotoException;

    public List<String> findUsers(String searchingPattern) throws P2PhotoException;

    public List<String> listUserAlbumSlices(int albumId) throws P2PhotoException;

    public List<Integer> listIncomingRequest() throws P2PhotoException;

    public void acceptIncomingRequest(int albumId, String cloudDirectoryPath) throws P2PhotoException;

    public void rejectIncomingRequest(int albumId) throws P2PhotoException;


    public void terminateConn() throws P2PhotoException;

    public void toggleDebugMode();

    public void reset() throws P2PhotoException;

    public String getClientAPIVersion();

    public String getServerAPIVersion() throws P2PhotoException;
}