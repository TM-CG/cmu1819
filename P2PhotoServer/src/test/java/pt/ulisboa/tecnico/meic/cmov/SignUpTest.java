package pt.ulisboa.tecnico.meic.cmov;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static pt.ulisboa.tecnico.meic.cmov.Instruction.*;

public class SignUpTest {

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
    public void simpleSignUp() {
        SignUp signUp = new SignUp(args, dummyServer);

        String response = signUp.execute();
        assertNotNull(response);

        assertEquals(OK, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());

        assertEquals("test_user1", dummyServer.getUserByUsername("test_user1").getUsername());
    }

    @Test
    public void simpleSignUpTwoUsers() {
        simpleSignUp();
        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add("test_user2");
        this.args.add("testpass2");

        SignUp signUp = new SignUp(args, dummyServer);

        String response = signUp.execute();
        assertNotNull(response);

        assertEquals(OK, response);

        assertEquals(2, dummyServer.numberOfRegisteredUsers());

        assertEquals("test_user2", dummyServer.getUserByUsername("test_user2").getUsername());
    }

    @Test
    public void signUpAlreadyExistentUser() {
        simpleSignUp();
        SignUp signUp = new SignUp(args, dummyServer);

        String response = signUp.execute();
        assertNotNull(response);

        assertEquals(NOK_3, response);

        assertEquals(1, dummyServer.numberOfRegisteredUsers());

        assertEquals("test_user1", dummyServer.getUserByUsername("test_user1").getUsername());
    }

    @Test
    public void signUpWithInvalidUserName() {

        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add("invalid username");
        this.args.add("testpass");

        SignUp signUp = new SignUp(args, dummyServer);

        String response = signUp.execute();
        assertNotNull(response);

        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredUsers());

    }

    @Test
    public void signUpWithInvalidUserName2() {

        this.args = new ArrayList<>();
        this.args.add("SIGNUP");
        this.args.add("invalid\"username\"");
        this.args.add("testpass");

        SignUp signUp = new SignUp(args, dummyServer);

        String response = signUp.execute();
        assertNotNull(response);

        assertEquals(ERR, response);

        assertEquals(0, dummyServer.numberOfRegisteredUsers());

    }

    @After
    public void tearDown() {
        dummyServer.reset();
    }

}