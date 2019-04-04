package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class UpdateAlbumTest {
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



    }

    @Test
    public void addUser2ToAlbum() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(OK_PLUS + albumId, response);

        assertEquals(2, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());
    }

    @Test
    public void updatingNonExistentAlbum() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add("10");
        this.args.add(TEST_USER_2);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(NOK_5, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());
    }

    @Test
    public void updatingAlbumWhichImNotTheOwner() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add("2");
        this.args.add(TEST_USER_3);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(NOK_6, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());
    }

    @Test
    public void updatingAlbumToAddMySelfWhereImTheOwner() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add(albumId);
        this.args.add(TEST_USER_1);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(NOK_6, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());
    }

    @Test
    public void updateAlbumWithInvalidSessionId() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add("1nv4l1ds35510n1d");
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(NOK_4, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

    }

    @Test
    public void updateAlbumWithNullSessionId() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(null);
        this.args.add(albumId);
        this.args.add(TEST_USER_2);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(NOK_4, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

    }

    @Test
    public void updateAlbumWithoutUserName() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(null);
        this.args.add(albumId);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(ERR, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

    }

    @Test
    public void updateAlbumWithInvalidAlbumId() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        this.args = new ArrayList<>();
        this.args.add("ALB-AUP");
        this.args.add(sessionId);
        this.args.add("-3");
        this.args.add(TEST_USER_2);

        UpdateAlbum upd8Album = new UpdateAlbum(args, dummyServer);
        String response = upd8Album.execute();

        assertEquals(NOK_5, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

    }

    @Test
    public void updateAlbumWithNullArgs() {

        //only the owner is the participant of this album for now
        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

        UpdateAlbum upd8Album = new UpdateAlbum(null, dummyServer);
        String response = upd8Album.execute();

        assertEquals(ERR, response);

        assertEquals(1, dummyServer.getAlbumById(Integer.parseInt(albumId)).getTotalNumberOfParticipants());

    }


    @After
    public void tearDown() {
        dummyServer.reset();
    }
}