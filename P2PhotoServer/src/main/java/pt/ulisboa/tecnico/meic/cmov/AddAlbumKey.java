package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class AddAlbumKey extends Instruction {
    public AddAlbumKey(List<String> args, Server server) {
        super("ALB-AUK", args, server);
    }

    @Override
    public String execute() {

        try {
            if (args.size() != 5)
                return ERR;

            String sessionId = args.get(1);
            String albumId = args.get(2);
            String username = args.get(3);
            String cipheredKey = args.get(4);

            String loggedInUser = server.getUserNameBySessionID(sessionId);

            if (loggedInUser == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            }

            if (cipheredKey == null) {
                return ERR;
            }

            Album album = server.getAlbumById(new Integer(albumId));
            if (album == null) {
                displayDebug(VERBOSE_NOK5);
                return NOK_5;
            }

            album.addAlbumKey(username, cipheredKey);
            displayDebug("User %s added a ciphered album key to album %s to allow %s to access", loggedInUser, username);
            return OK_PLUS + albumId;

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
