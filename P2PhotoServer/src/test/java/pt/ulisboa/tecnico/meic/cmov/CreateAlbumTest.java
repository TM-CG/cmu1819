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
        this.args.add("https://user.p2photocloud.com/user");

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
        this.args.add("Album de teste");

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
        this.args.add("Album de teste");

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
        this.args.add("Album de teste");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(NOK_4, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createAlbumWithInvalidTitle() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add("1nv4l1ds35510n1d");
        this.args.add("Album de \"teste\"");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createAlbumWithNullSessionId() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(null);
        this.args.add("Album de \"teste\"");

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

    }

    @Test
    public void createAlbumWithoutTitle() {

        this.args = new ArrayList<>();

        this.args.add("ALB-CR8");
        this.args.add(sessionId);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

        CreateAlbum cr8Album = new CreateAlbum(args, dummyServer);
        String response = cr8Album.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredAlbums());

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

    @After
    public void tearDown() {
        dummyServer.reset();
    }

}