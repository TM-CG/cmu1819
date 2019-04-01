package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class LogIn extends Instruction {

    LogIn(List<String> args, Server server) {
        super("LOGIN", args, server);
    }

    @Override
    public String execute() {

        try {

            String username = args.get(1);
            String password = args.get(2);
            User user;
            String sessionId;

            if (username == null || password == null || username.contains(" ") || password.contains(" ") || username.contains("\"") || password.contains("\""))
                return "ERR";

            if (!server.usernameExists(username)) {
                displayDebug(NOK1, username);
                return "NOK 1";
            } else {
                user = server.getUserByUsername(username);
                if (!user.getPassword().equals(password)) {
                    displayDebug(NOK2, username);
                    return "NOK 2";
                } else {

                    sessionId = server.usernameIsLoggedOn(username);

                    if (sessionId == null) {
                        sessionId = Long.toHexString(Double.doubleToLongBits(Math.random()));
                        server.addLoggedUser(username, sessionId);
                    }
                    return "OK " + sessionId;


                }

            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return "ERR";
        }
    }
}
