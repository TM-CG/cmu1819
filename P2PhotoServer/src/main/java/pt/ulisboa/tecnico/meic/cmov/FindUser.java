package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class FindUser extends Instruction {

    FindUser(List<String> args, Server server) {
        super("USR-FND", args, server);
    }

    @Override
    public String execute() {
        String sessionId = args.get(1);
        String pattern = args.get(2);
        List<String> matches;

        if (server.getUserNameBySessionID(sessionId) == null) {
            displayDebug(NOK4);
            return "NOK 4";
        }
        else {

            if (pattern.contains("*")) {
                matches = server.findUserNameByPattern("\\b(\\w*" + pattern.replace("*", "") + "\\w*)\\b");
            } else matches = server.findUserNameByPattern("\\b(\\w*" + pattern + "\\w*)\\b");

            return "OK " + server.representList(matches);
        }
    }
}
