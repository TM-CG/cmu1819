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
            displayDebug(NOK4);
            return "NOK 4";
        } else {
            album = server.getAlbumById(new Integer(albumId));
            //Checks if album ID exists
            if (album == null) {
                displayDebug(NOK5);
                return "NOK 5";
            }

            album = server.getAlbumById(new Integer(albumId));
            return "OK " + server.representList(album.getAlbumSlicesURLs());
        }
    }
}
