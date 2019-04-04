package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.support.v4.util.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ServerConnectorTest {

    private ServerConnector serverConnector;

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

        serverConnector.createAlbum("Album de teste");
        serverConnector.updateAlbum(1, "ze");

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

    @After
    public void tearDown() throws P2PhotoException {
        serverConnector.logOut();
        serverConnector.reset();
        serverConnector.terminateConn();
    }
}