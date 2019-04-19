package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class DisplayAlbumOwnerTest {

    private static final String TEST_USER_3 = "test_user3";
    private static final String TEST_USER_2 = "test_user2";
    private static final String TEST_USER_1 = "test_user1";
    private static final String TESTPASS_3 = "testpass3";
    private static final String TESTPASS_2 = "testpass2";
    private static final String TESTPASS_1 = "testpass1";
    private static final String CLOUD_USER_1 = "https://cloud.com/user1";
    private static final String CLOUD_USER_2 = "https://cloud.com/user2";

    private Server dummyServer;

    private List<String> args;

    private String sessionId;
    private String anotherSessionId;

    private String albumId;
    private String albumId2;

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        this.args = new ArrayList<>();

        //User1
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_1);
        this.args.add(TESTPASS_1);

        new SignUp(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add(TEST_USER_1);
        this.args.add(TESTPASS_1);

        sessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        //create album de teste
        this.args = new ArrayList<>();
        this.args.add("ALB-CR8");
        this.args.add(sessionId);
        this.args.add(CLOUD_USER_1);

        albumId = new CreateAlbum(args, dummyServer).execute().split(" ")[1];

        //User2
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_2);
        this.args.add(TESTPASS_2);

        new SignUp(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add(TEST_USER_2);
        this.args.add(TESTPASS_2);

        anotherSessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        //create album de teste
        this.args = new ArrayList<>();
        this.args.add("ALB-CR8");
        this.args.add(anotherSessionId);
        this.args.add(CLOUD_USER_2);

        albumId2 = new CreateAlbum(args, dummyServer).execute().split(" ")[1];


    }

    @Test
    public void simpleDisplayAlbumOwner() {
        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(sessionId);
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + TEST_USER_1, response);

    }

    @Test
    public void DisplayAlbumOwnerInvalidAlbum() {
        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(sessionId);
        this.args.add("54");

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(NOK_5, response);

    }

    @Test
    public void DisplayAlbumOwnerWithNullSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(null);
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);

    }

    @Test
    public void DisplayAlbumOwnerWithoutSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

    }

    @Test
    public void DisplayAlbumOwnerWithNullArgs() {

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(null, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

    }

    @Test
    public void DisplayAlbumOwnerWithOutPermission() {
        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(anotherSessionId);
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(NOK_5, response);

    }

    @Test
    public void DisplayAlbumOwnerWithPendingUser() {
        //add user 2 to album1
        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        new UpdateAlbum(args, dummyServer).execute();


        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(anotherSessionId);
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + TEST_USER_1, response);

    }

    @Test
    public void DisplayAlbumOwnerWithParticipating() {
        //add user 2 to album1
        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        new UpdateAlbum(args, dummyServer).execute();

        //user 2 accepts to participate on album 1
        this.args = new ArrayList<>();
        this.args.add("USR-URQ");
        this.args.add(anotherSessionId);
        this.args.add("A");
        this.args.add(albumId);
        this.args.add(CLOUD_USER_2);

        new UpdateRequests(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(anotherSessionId);
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + TEST_USER_1, response);

    }

    @Test
    public void DisplayAlbumOwnerAfterUserRejects() {
        //add user 2 to album1
        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        new UpdateAlbum(args, dummyServer).execute();

        //user 2 rejects to participate on album 1
        this.args = new ArrayList<>();
        this.args.add("USR-URQ");
        this.args.add(anotherSessionId);
        this.args.add("R");
        this.args.add(albumId);

        new UpdateRequests(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("ALB-OWN");
        this.args.add(anotherSessionId);
        this.args.add(albumId);

        DisplayAlbumOwner displayAlbumOwner = new DisplayAlbumOwner(args, dummyServer);
        String response = displayAlbumOwner.execute();

        assertNotNull(response);

        assertEquals(NOK_5, response);

    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }
}