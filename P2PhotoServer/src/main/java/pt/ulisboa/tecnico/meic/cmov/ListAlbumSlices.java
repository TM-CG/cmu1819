package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class ListAlbumSlices extends Instruction {

    ListAlbumSlices(List<String> args, Server server) {
        super("ALB-UAS", args, server);
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
                //Checks if album ID exists and if the current user has permission to access
                if ((album == null) || (album.getIndexOfUser(username) == null)) {
                    displayDebug(VERBOSE_NOK5);
                    return NOK_5;
                }

                album = server.getAlbumById(new Integer(albumId));

                displayDebug("User %s requested list album slices from album %s", username, albumId);
                return OK_PLUS + server.representList(album.getAlbumSlicesURLs());
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
