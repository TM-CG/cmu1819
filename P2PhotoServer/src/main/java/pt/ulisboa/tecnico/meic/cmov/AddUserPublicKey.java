package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class AddUserPublicKey extends Instruction{

    public AddUserPublicKey(List<String> args, Server server) {
        super("USR-APK", args, server);
    }

    @Override
    public String execute() {

        try {

            if (args.size() != 3)
                return ERR;

            String sessionId = args.get(1);
            String publicKey = args.get(2);
            String username = server.getUserNameBySessionID(sessionId);

            if (username == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            }

            if (publicKey == null) {
                return ERR;
            }

            User user = server.getUserByUsername(username);
            user.setPublicKey(publicKey);

            displayDebug("User %s added its own public key", username);
            return OK;
        }
        catch (NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
