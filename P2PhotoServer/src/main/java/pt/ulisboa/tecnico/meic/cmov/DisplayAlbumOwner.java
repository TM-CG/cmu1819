package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

import static pt.ulisboa.tecnico.meic.cmov.Album.NOT_AVAILABLE_URL;

public class DisplayAlbumOwner extends Instruction {

    DisplayAlbumOwner(List<String> args, Server server) {
        super("ALB-OWN", args, server);
    }

    @Override
    public String execute() {

        try {

            if (args.size() != 3)
                return "ERR";

            String sessionId = args.get(1);
            String albumId = args.get(2);

            String username = server.getUserNameBySessionID(sessionId);
            Album album;

            if (username == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {
                album = server.getAlbumById(new Integer(albumId));

                //if album does not exists
                if (album == null)  {
                    displayDebug(VERBOSE_NOK5);
                    return NOK_5;
                } else {
                    String index = album.getIndexOfUser(username);

                    //user is on pending state
                    if (index == null || !index.equals(NOT_AVAILABLE_URL)) {

                        String owner = album.getOwner();

                        displayDebug("User %s requested to know the owner of album %s which is %s", username,
                                albumId, owner);

                        return OK_PLUS + owner;

                    } else { //the user is not on pending state or participates
                        displayDebug(VERBOSE_NOK5);
                        return NOK_5;
                    }
                }

            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
