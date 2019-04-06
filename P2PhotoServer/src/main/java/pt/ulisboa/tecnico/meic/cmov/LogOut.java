package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class LogOut extends Instruction {

    LogOut(List<String> args, Server server) {
        super("LOGOUT", args, server);
    }

    @Override
    public String execute() {

        try {

            String sessionId = args.get(1);
            String username = server.getUserNameBySessionID(sessionId);

            if (sessionId == null)
                return ERR;

            if (username == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {
                server.removeLoggedUser(username, sessionId);
                displayDebug("User %s has just logged off successfully!", username);
                return OK;
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
