package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class LogInTest {

    private Server dummyServer;

    private List<String> args;

    @Before
    public void setUp() {
        this.dummyServer = new Server();
        this.args = new ArrayList<>();

        this.args.add("SIGNUP");
        this.args.add("test_user1");
        this.args.add("testpass1");
        new SignUp(args, dummyServer).execute();

        //prepare args for login
        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add("test_user1");
        this.args.add("testpass1");

    }

    @Test
    public void simpleLogIn() {
        LogIn login = new LogIn(args, dummyServer);

        String response;

        response = login.execute();

        assertNotNull(response);

        if (!response.startsWith(OK))
            fail();

    }

    @Test
    public void loginWithNonExistentUser() {

        this.args = new ArrayList<>();
        this.args.add("LOGIN");
        this.args.add("ze_naoexiste");
        this.args.add("pass_do_ze");

        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        if (response.startsWith(OK))
            fail();

    }

    @Test
    public void LoginSameUserTwice() {
        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        if (!response.startsWith(OK))
            fail();

        String sessionId = response.split(" ")[1];

        LogIn login2 = new LogIn(args, dummyServer);

        String response2 = login.execute();

        assertNotNull(response2);

        if (!response2.startsWith(OK))
            fail();

        if (!response2.split(" ")[1].equals(sessionId))
            fail(); //same login same sessionId
    }

    @Test
    public void wrongPasswordLogin() {

        this.args = new ArrayList<>();

        this.args.add("LOGIN");
        this.args.add("test_user1");
        this.args.add("wrongpass1");

        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        assertEquals(NOK_2, response);
    }

    @Test
    public void InvalidUserNameLogin() {

        this.args = new ArrayList<>();

        this.args.add("LOGIN");
        this.args.add("test user1");
        this.args.add("wrongpass1");

        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        assertEquals(ERR, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }
}