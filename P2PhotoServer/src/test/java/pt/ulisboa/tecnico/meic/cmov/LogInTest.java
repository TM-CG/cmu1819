package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

    }

    @Test
    public void simpleLogIn() {
        SignUp signUp = new SignUp(args, dummyServer);

        String response = signUp.execute();
        assertNotNull(response);

        assertEquals("OK", response);

        LogIn login = new LogIn(args, dummyServer);

        response = null;

        response = login.execute();

        assertNotNull(response);

        if (!response.startsWith("OK"))
            fail();

    }

    @Test
    public void loginWithNonExistentUser() {
        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        if (response.startsWith("OK"))
            fail();

    }

    @Test
    public void LoginSameUserTwice() {
        new SignUp(args, dummyServer).execute();

        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        if (!response.startsWith("OK"))
            fail();

        String sessionId = response.split(" ")[1];

        LogIn login2 = new LogIn(args, dummyServer);

        String response2 = login.execute();

        assertNotNull(response2);

        if (!response2.startsWith("OK"))
            fail();

        if (!response2.split(" ")[1].equals(sessionId))
            fail(); //same login same sessionId
    }

    @Test
    public void wrongPasswordLogin() {
        new SignUp(args, dummyServer).execute();

        this.args = new ArrayList<>();

        this.args.add("LOGIN");
        this.args.add("test_user1");
        this.args.add("wrongpass1");

        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        assertEquals("NOK 2", response);
    }

    @Test
    public void InvalidUserNameLogin() {
        new SignUp(args, dummyServer).execute();

        this.args = new ArrayList<>();

        this.args.add("LOGIN");
        this.args.add("test user1");
        this.args.add("wrongpass1");

        LogIn login = new LogIn(args, dummyServer);

        String response = login.execute();

        assertNotNull(response);

        assertEquals("ERR", response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());
    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }
}