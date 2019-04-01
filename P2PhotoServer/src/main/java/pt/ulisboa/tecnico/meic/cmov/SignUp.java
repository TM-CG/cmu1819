package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class SignUp extends Instruction {

    SignUp(List<String> args, Server server) {
        super("SIGNUP", args, server);
    }

    @Override
    public String execute() {
        try {

            if (args.size() != 4)
                return "ERR";

            String username = args.get(1);
            String password = args.get(2);
            String url = args.get(3);

            if (username == null || password == null || username.contains(" ") || password.contains(" ") || username.contains("\"") || password.contains("\""))
                return ERR;

            //Check if user exists
            if (server.usernameExists(username)) {
                displayDebug(VERBOSE_NOK3, username);
                return NOK_3;
            } else {
                //Adds user
                server.addUser(new User(username, password, url));
                displayDebug("Successfully added user " + username);
                return OK;
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
