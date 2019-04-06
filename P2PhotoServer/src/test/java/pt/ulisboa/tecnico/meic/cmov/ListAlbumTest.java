package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class ListAlbumTest {

    private static final String TEST_USER_3 = "test_user3";
    private static final String TEST_USER_2 = "test_user2";
    private static final String TEST_USER_1 = "test_user1";
    private static final String TESTPASS_3 = "testpass3";
    private static final String TESTPASS_2 = "testpass2";
    private static final String TESTPASS_1 = "testpass1";
    private static final String ALBUM_DE_TESTE = "https://cloud.com/teste";
    private static final String ALBUM_SEGUNDO = "https://cloud.com/seg";
    private static final String ALBUM_TERCEIRO = "https://cloud.com/terceiro";

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

        this.args = new ArrayList<>();
        this.args.add("LOGIN");
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
    public void simpleList() {
        this.args = new ArrayList<>();
        this.args.add("ALB-LST");
        this.args.add(sessionId);

        ListAlbum listAlbum = new ListAlbum(args, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<" + 1 + ">", response);
    }

    @Test
    public void simpleListAfterSecondUserCr8Directory() {

        //Simulation of file creation on server
        Album album = dummyServer.getAlbumById(2);
        album.setIndexOfParticipant(TEST_USER_1, "https://cloud.com/caminho/album2");

        this.args = new ArrayList<>();
        this.args.add("ALB-LST");
        this.args.add(sessionId);

        ListAlbum listAlbum = new ListAlbum(args, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<" + 1 + " , " + 2  + ">", response);
    }

    @Test
    public void AlbumListWithInvalidSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-LST");
        this.args.add("1nv4l1ds35510n1d");

        ListAlbum listAlbum = new ListAlbum(args, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);
    }

    @Test
    public void AlbumListWithNullSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-LST");
        this.args.add(null);

        ListAlbum listAlbum = new ListAlbum(args, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);
    }

    @Test
    public void AlbumListWithoutSessionId() {
        this.args = new ArrayList<>();
        this.args.add("ALB-LST");

        ListAlbum listAlbum = new ListAlbum(args, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(ERR, response);
    }


    @Test
    public void AlbumListWithNullArgs() {
        ListAlbum listAlbum = new ListAlbum(null, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(ERR, response);
    }

    @Test
    public void AlbumListEmpty() {

        //User2
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add("ze_sem_albums");
        this.args.add(TESTPASS_2);

        new SignUp(args, dummyServer).execute();

        //Login to another user
        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add("ze_sem_albums");
        this.args.add(TESTPASS_2);

        String anotherSessionId = new LogIn(args, dummyServer).execute().split(" ")[1];

        this.args = new ArrayList<>();
        this.args.add("ALB-LST");
        this.args.add(anotherSessionId);

        ListAlbum listAlbum = new ListAlbum(args, dummyServer);
        String response = listAlbum.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<>", response);
    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }

}