package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class UpdateRequestsTest {
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

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        this.args = new ArrayList<>();

        //User3
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_3);
        this.args.add(TESTPASS_3);

        new SignUp(args, dummyServer).execute();

        //User2
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_2);
        this.args.add(TESTPASS_2);

        new SignUp(args, dummyServer).execute();

        //User1
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_1);
        this.args.add(TESTPASS_1);

        new SignUp(args, dummyServer).execute();

        //User1
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_1);
        this.args.add(TESTPASS_1);

        sessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        //create album de teste
        this.args = new ArrayList<>();
        this.args.add("ALB-CR8");
        this.args.add(sessionId);
        this.args.add(CLOUD_USER_1);

        albumId = new CreateAlbum(args, dummyServer).execute().split(" ")[1];

        //Login to another user
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

        new CreateAlbum(args, dummyServer).execute();

        //add user 2 to album1
        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        new UpdateAlbum(args, dummyServer).execute();
    }

    @Test
    public void simpleAcceptRequest() {
        this.args = new ArrayList<>();
        this.args.add("USR-URQ");
        this.args.add(anotherSessionId);
        this.args.add("A");
        this.args.add(albumId);
        this.args.add(CLOUD_USER_2);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfParticipants());
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfPendingParticipants());

        UpdateRequests updateRequests = new UpdateRequests(args, dummyServer);
        String response = updateRequests.execute();

        assertNotNull(response);
        assertEquals(OK_PLUS + albumId, response);

        assertEquals(2, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfParticipants());
        assertEquals(0, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfPendingParticipants());
    }

    @Test
    public void acceptRequestWithNullArgs() {

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfParticipants());
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfPendingParticipants());

        UpdateRequests updateRequests = new UpdateRequests(null, dummyServer);
        String response = updateRequests.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfParticipants());
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfPendingParticipants());
    }

    @Test
    public void AcceptRequestWrongParametersOrder() {

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfParticipants());
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfPendingParticipants());

        this.args = new ArrayList<>();
        this.args.add("USR-URQ");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add("A");
        this.args.add(CLOUD_USER_1);

        UpdateRequests updateRequests = new UpdateRequests(args, dummyServer);
        String response = updateRequests.execute();

        assertNotNull(response);
        assertEquals(ERR, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfParticipants());
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getNumberOfPendingParticipants());
    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }
}