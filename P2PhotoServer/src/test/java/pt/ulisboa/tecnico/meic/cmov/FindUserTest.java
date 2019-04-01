package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class FindUserTest {

    private static final String TEST_USER_3 = "test_user3";
    private static final String TEST_USER_2 = "test_user2";
    private static final String TEST_USER_1 = "test_user1";

    private Server dummyServer;

    private List<String> args;

    private String sessionId;

    private String albumId;

    private void addUser(String username) {
        this.args = new ArrayList<>();
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(username);
        this.args.add("passdefault");
        this.args.add("https://user.p2photocloud.com/user");
        new SignUp(args, dummyServer).execute();
    }

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        addUser(TEST_USER_1);
        addUser(TEST_USER_2);
        addUser(TEST_USER_3);

        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add(TEST_USER_1);
        this.args.add("passdefault");

        sessionId = new LogIn(args, dummyServer).execute().split(" ")[1];
    }

    @Test
    public void simpleFindUser() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add(sessionId);
        this.args.add(TEST_USER_1);

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<" + TEST_USER_1 + ">", response);

    }

    @Test
    public void FindUserPattern() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add(sessionId);
        this.args.add("test_*");

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<" + TEST_USER_1 + " , " + TEST_USER_2 + " , " + TEST_USER_3 + ">", response);

    }

    @Test
    public void FindAllUsers() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add(sessionId);
        this.args.add("*");

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<" + TEST_USER_1 + " , " + TEST_USER_2 + " , " + TEST_USER_3 + ">", response);

    }

    @Test
    public void FindUserWithNullArgs() {
        FindUser findUser = new FindUser(null, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

    }

    @Test
    public void FindUsersEmptyResponse() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add(sessionId);
        this.args.add("almerindo");

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(OK_PLUS + "<>", response);

    }

    @Test
    public void FindUsersWithWrongSessionId() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add("1nv4l1d535510n1d");
        this.args.add("test_*");

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);

    }

    @Test
    public void FindUserWithoutSessionId() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add(TEST_USER_1);

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

    }

    @Test
    public void FindUserWithoutPattern() {

        this.args = new ArrayList<>();
        this.args.add("USR-FND");
        this.args.add(sessionId);

        FindUser findUser = new FindUser(args, dummyServer);
        String response = findUser.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }
}