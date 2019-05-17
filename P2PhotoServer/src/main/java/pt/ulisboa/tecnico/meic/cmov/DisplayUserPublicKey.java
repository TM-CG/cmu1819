package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class DisplayUserPublicKey extends Instruction {

    DisplayUserPublicKey(List<String> args, Server server) {
        super("USR-PUB", args, server);
    }

    @Override
    public String execute() {
        try {

            if (args.size() != 3)
                return ERR;

            String sessionId = args.get(1);
            String userToSearch = args.get(2);
            String username = server.getUserNameBySessionID(sessionId);
            User user = server.getUserByUsername(userToSearch);

            if (username == null)
                return NOK_4;

            if (user == null)
                return NOK_1;

            return user.getPublicKey();

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
