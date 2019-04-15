package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ServerConnectorTest {

    private ServerConnector serverConnector;
    private ServerConnector sv2;

    @Before
    public void setUp() throws P2PhotoException {
        serverConnector = new ServerConnector("localhost", 10000);
        serverConnector.toggleDebugMode(); //show debug messages

        serverConnector.signUp("vitor", "mypass");
        serverConnector.signUp("ze", "zepass");
        serverConnector.logIn("vitor", "mypass");
    }

    @Test
    public void simpleCreateUpdateAlbum() throws P2PhotoException {

        serverConnector.createAlbum("https://cloud.com/album");
        serverConnector.updateAlbum(1, "ze");

        sv2 = new ServerConnector("localhost", 10000);
        sv2.toggleDebugMode();
        sv2.logIn("ze", "zepass");
        sv2.acceptIncomingRequest(1, "https://cloudDoZe.com/");
        sv2.createAlbum("https://cloud.com/albumDoZe");

    }

    @Test
    public void findUsers() throws P2PhotoException {
        List<String> usernames = serverConnector.findUsers("*");
        assertNotNull(usernames);

        assertEquals(2, usernames.size());
        assertEquals("vitor", usernames.get(0));
        assertEquals("ze", usernames.get(1));
    }

    @Test
    public void findUsersWildCard() throws P2PhotoException {
        List<String> usernames = serverConnector.findUsers("z*");
        assertNotNull(usernames);

        assertEquals(1, usernames.size());
        assertEquals("ze", usernames.get(0));
    }

    @Test
    public void findUsersCompleteName() throws P2PhotoException {
        List<String> usernames = serverConnector.findUsers("vitor");
        assertNotNull(usernames);

        assertEquals(1, usernames.size());
        assertEquals("vitor", usernames.get(0));
    }

    /*@Test
    public void listUserAlbums() throws P2PhotoException {
        List<Integer> albums = serverConnector.listUserAlbums();
        assertNotNull(albums);

        assertEquals(0, albums.size());
        simpleCreateUpdateAlbum();

        albums = serverConnector.listUserAlbums();
        assertNotNull(albums);

        assertEquals(1, albums.size());

        assertEquals(new Integer(1), albums.get(0));
    }*/

    @Test
    public void testListUserAlbumSlices() throws P2PhotoException {
        simpleCreateUpdateAlbum();

        List<String> urls = serverConnector.listUserAlbumSlices(1);
        assertNotNull(urls);

        assertEquals(2, urls.size());
        /*assertEquals("https://cloud.org/vitor/album_de_teste_1.alb", urls.get(0));
        assertEquals("https://cloud.org/ze/album_de_teste_1.alb", urls.get(1));*/

    }

    @Test
    public void testListAlbumWithOptionsDefault() throws P2PhotoException {
        simpleCreateUpdateAlbum();

        List<Integer> urls = serverConnector.listUserAlbums();
        assertNotNull(urls);

        assertEquals(1, urls.size());

        List<Integer> urls2 = sv2.listUserAlbums();
        assertNotNull(urls2);

        assertEquals(2, urls2.size());

    }

    @Test
    public void testListAlbumWithOptionsAll() throws P2PhotoException {
        simpleCreateUpdateAlbum();

        List<Integer> urls = serverConnector.listUserAlbums(ServerConnector.ListAlbumOption.VIEW_ALL);
        assertNotNull(urls);

        assertEquals(1, urls.size());

        List<Integer> urls2 = sv2.listUserAlbums(ServerConnector.ListAlbumOption.VIEW_ALL);
        assertNotNull(urls2);

        assertEquals(2, urls2.size());

    }

    @Test
    public void testListAlbumWithOptionsOwn() throws P2PhotoException {
        simpleCreateUpdateAlbum();

        List<Integer> urls = serverConnector.listUserAlbums(ServerConnector.ListAlbumOption.VIEW_OWN);
        assertNotNull(urls);

        assertEquals(1, urls.size());

        List<Integer> urls2 = sv2.listUserAlbums(ServerConnector.ListAlbumOption.VIEW_OWN);
        assertNotNull(urls2);

        assertEquals(1, urls2.size());

    }

    @Test
    public void testListAlbumWithOptionsPar() throws P2PhotoException {
        simpleCreateUpdateAlbum();

        List<Integer> urls = serverConnector.listUserAlbums(ServerConnector.ListAlbumOption.VIEW_PAR);
        assertNotNull(urls);

        assertEquals(0, urls.size());

        List<Integer> urls2 = sv2.listUserAlbums(ServerConnector.ListAlbumOption.VIEW_PAR);
        assertNotNull(urls2);

        assertEquals(1, urls2.size());

    }

    @After
    public void tearDown() throws P2PhotoException {
        serverConnector.logOut();
        serverConnector.reset();
        serverConnector.terminateConn();
    }
}