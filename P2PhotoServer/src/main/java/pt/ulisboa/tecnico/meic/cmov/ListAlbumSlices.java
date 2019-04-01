package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class ListAlbumSlices extends Instruction {

    ListAlbumSlices(List<String> args, Server server) {
        super("ALB-UAS", args, server);
    }

    @Override
    public String execute() {
        String sessionId = args.get(1);
        String albumId = args.get(2);

        String username = server.getUserNameBySessionID(sessionId);
        Album album;

        if (username == null) {
            displayDebug(VERBOSE_NOK4);
            return NOK_4;
        } else {
            album = server.getAlbumById(new Integer(albumId));
            //Checks if album ID exists
            if (album == null) {
                displayDebug(VERBOSE_NOK5);
                return NOK_5;
            }

            album = server.getAlbumById(new Integer(albumId));
            return OK_PLUS + server.representList(album.getAlbumSlicesURLs());
        }
    }
}
