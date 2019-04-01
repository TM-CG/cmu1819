package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class LogOutTest {

    private Server dummyServer;

    private List<String> args;

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        this.args = new ArrayList<>();

        this.args.add("SIGNUP");
        this.args.add("test_user1");
        this.args.add("testpass1");
        this.args.add("https://user.p2photocloud.com/user");

        new SignUp(args, dummyServer).execute();

        //prepare for logins
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add("test_user1");
        this.args.add("testpass1");

    }

    @Test
    public void simpleLogOut() {

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
        assertEquals(0, dummyServer.numberOfLoggedInUsers());

        String response = new LogIn(args, dummyServer).execute();

        assertEquals(1, dummyServer.numberOfLoggedInUsers());

        this.args = new ArrayList<>();
        this.args.add("LOGOUT");
        this.args.add(response.split(" ")[1]); //sessionId

        LogOut logout = new LogOut(args, dummyServer);
        response = logout.execute();

        assertNotNull(response);

        assertEquals(OK, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
        assertEquals(0, dummyServer.numberOfLoggedInUsers());

    }

    @Test
    public void LogOutWithInvalidSessionId() {
        new LogIn(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("LOGOUT");
        this.args.add("1nv4l1ds35510n1d"); //invalid sessionId

        LogOut logout = new LogOut(args, dummyServer);
        String response = logout.execute();

        assertNotNull(response);

        assertEquals(NOK_4, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
        assertEquals(1, dummyServer.numberOfLoggedInUsers());

    }

    @Test
    public void LogOutWithInvalidSessionId2() {
        new LogIn(args, dummyServer).execute();

        this.args = new ArrayList<>();
        this.args.add("LOGOUT");

        LogOut logout = new LogOut(args, dummyServer);
        String response = logout.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
        assertEquals(1, dummyServer.numberOfLoggedInUsers());

    }

    @Test
    public void LogOutWithNullArgs() {
        new LogIn(args, dummyServer).execute();

        LogOut logout = new LogOut(null, dummyServer);
        String response = logout.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
        assertEquals(1, dummyServer.numberOfLoggedInUsers());

    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }

}