package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class ListAlbumSlicesTest {

    private static final String TEST_USER_3 = "test_user3";
    private static final String TEST_USER_2 = "test_user2";
    private static final String TEST_USER_1 = "test_user1";
    private static final String TESTPASS_3 = "testpass3";
    private static final String TESTPASS_2 = "testpass2";
    private static final String TESTPASS_1 = "testpass1";
    private static final String ALBUM_DE_TESTE = "Album de teste";
    private static final String ALBUM_SEGUNDO = "Album segundo";
    private static final String ALBUM_TERCEIRO = "Album terceiro";
    private static final String CLOUD_USER_1 = "https://user.p2photocloud.com/user1";
    private static final String CLOUD_USER_2 = "https://user.p2photocloud.com/user2";
    private static final String CLOUD_USER_3 = "https://user.p2photocloud.com/user3";

    private Server dummyServer;

    private List<String> args;

    private String sessionId;

    private String albumId;

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        this.args = new ArrayList<>();

        //User3
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_3);
        this.args.add(TESTPASS_3);
        this.args.add(CLOUD_USER_3);

        new SignUp(args, dummyServer).execute();

        //User2
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_2);
        this.args.add(TESTPASS_2);
        this.args.add(CLOUD_USER_2);

        new SignUp(args, dummyServer).execute();

        //User1
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_1);
        this.args.add(TESTPASS_1);
        this.args.add(CLOUD_USER_1);

        new SignUp(args, dummyServer).execute();

        //User1 for login
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_1);
        this.args.add(TESTPASS_1);
        sessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        //create album de teste
        this.args = new ArrayList<>();
        this.args.add("ALB-CR8");
        this.args.add(sessionId);
        this.args.add(ALBUM_DE_TESTE);

        albumId = new CreateAlbum(args, dummyServer).execute().split(" ")[1];

        //Login to another user
        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add(TEST_USER_2);
        this.args.add(TESTPASS_2);

        String anotherSessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        //create album de teste
        this.args = new ArrayList<>();
        this.args.add("ALB-CR8");
        this.args.add(anotherSessionId);
        this.args.add(ALBUM_SEGUNDO);

        new CreateAlbum(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(anotherSessionId);
        this.args.add("2");
        this.args.add(TEST_USER_1);
        new UpdateAlbum(args, dummyServer).execute();

        //create album de teste
        this.args = new ArrayList<>();
        this.args.add("ALB-CR8");
        this.args.add(anotherSessionId);
        this.args.add(ALBUM_TERCEIRO);

        new CreateAlbum(args, dummyServer).execute();


    }

    @Test
    public void simpleListAlbumSlices() {
        this.args = new ArrayList<>();
        this.args.add("ALB-UAS");
        this.args.add(sessionId);
        this.args.add("1");

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(args, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<" + CLOUD_USER_1 + "/" + ALBUM_DE_TESTE.toLowerCase().replace(" ", "_") +
                "_1.alb>", response);
    }

    @Test
    public void AlbumSliceOfNonExistentAlbum() {
        this.args = new ArrayList<>();
        this.args.add("ALB-UAS");
        this.args.add(sessionId);
        this.args.add("10");

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(args, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(NOK_5, response);
    }

    @Test
    public void AlbumSliceUsingInvalidSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-UAS");
        this.args.add("1nv4l1d535510n1d");
        this.args.add("1");

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(args, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);
    }

    @Test
    public void AlbumSliceUsingNullSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-UAS");
        this.args.add(null);
        this.args.add("1");

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(args, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);
    }

    @Test
    public void AlbumSliceWithoutSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-UAS");
        this.args.add("1");

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(args, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(ERR, response);
    }

    @Test
    public void AlbumSliceOfAnAlbumImNotTheOwner() {

        //user 3 has no albums but tries to read paths for album 1
        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add(TEST_USER_3);
        this.args.add(TESTPASS_3);
        String anotherSessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        this.args = new ArrayList<>();
        this.args.add("ALB-UAS");
        this.args.add(anotherSessionId);
        this.args.add("1");

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(args, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(NOK_5, response);
    }

    @Test
    public void AlbumSliceWithNullArgs() {

        ListAlbumSlices listAlbumSlices = new ListAlbumSlices(null, dummyServer);
        String response = listAlbumSlices.execute();

        assertNotNull(response);

        assertEquals(ERR, response);
    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }
}