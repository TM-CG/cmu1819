package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class CreateAlbumTest {
    private Server dummyServer;

    private List<String> args;

    private String sessionId;

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        this.args = new ArrayList<>();

        this.args.add("SIGNUP");
        this.args.add("test_user1");
        this.args.add("testpass1");

        new SignUp(args, dummyServer).execute();

        this.args = new ArrayList<>();

        this.args.add("SIGNUP");
        this.args.add("test_user2");
        this.args.add("testpass2");

        new SignUp(args, dummyServer).execute();

        this.args = new ArrayList<>();

        this.args.add("SIGNUP");
        this.args.add("test_user1");
        this.args.add("testpass1");

        sessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

    }

    @Test
    public void createSimpleAlbum() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(sessionId);
        this.args.add("http://cloud.com/album1");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(OK_PLUS + "1", response);

        assertEquals(1, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createTwoAlbumsSameTitle() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(sessionId);
        this.args.add("http://cloud.com/album1");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(OK_PLUS + "1", response);

        assertEquals(1, dummyServer.numberOfRegisteredAlbums());

        cr8Album = new CreateAlbum(args, dummyServer);
        response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(OK_PLUS + "2", response);

        assertEquals(2, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createAlbumWithInvalidSessionId() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add("1nv4l1ds35510n1d");
        this.args.add("http://cloud.com/album1");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(NOK_4, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

    }


    @Test
    public void createAlbumWithNullSessionId() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(null);
        this.args.add("http://cloud.com/album1");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createAlbumWithoutURL() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(sessionId);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(OK_PLUS + 1, response);

        assertEquals(1, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createAlbumWithNullArgs() {

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(null, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

    }

    /**
     * Test for vulnerability related to commit #83:
     * User A creates album and adds user B at creation to participate.
     * User B wrongly becomes the owner of album.
     */
    @Test
    public void VulnerabilityTest_83() {
        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(sessionId);

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(OK_PLUS + "1", response);

        assertEquals(1, dummyServer.numberOfRegisteredAlbums());

        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(sessionId);
        this.args.add("1");

        String realOwnerResponse = new DisplayAlbumOwner(args, dummyServer).execute();
        assertNotNull(realOwnerResponse);
        assertEquals(OK_PLUS + "test_user1", realOwnerResponse);

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add("1");
        this.args.add("test_user2");

        String albumUpdateResponse = new UpdateAlbum(args, dummyServer).execute();
        assertNotNull(albumUpdateResponse);
        assertEquals(OK_PLUS + "1", albumUpdateResponse);

        this.args = new ArrayList<>();
        this.args.add("USR-URQ");
        this.args.add(sessionId);
        this.args.add("A");
        this.args.add("1");
        this.args.add("http://cloudexample.com/user1");

        String updateOwnRequest = new UpdateRequests(args, dummyServer).execute();
        assertNotNull(updateOwnRequest);
        assertEquals(OK_PLUS + "1", updateOwnRequest);

        //Checks the owner again
        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(sessionId);
        this.args.add("1");

        String onwerResponse = new DisplayAlbumOwner(args, dummyServer).execute();
        assertNotNull(onwerResponse);
        assertEquals(realOwnerResponse, onwerResponse);
    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }

}