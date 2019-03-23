package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class UpdateAlbum extends Instruction {

    UpdateAlbum(List<String> args, Server server) {
        super("ALB-AUP", args, server);
    }

    @Override
    public String execute() {
        String sessionId = args.get(1);
        String albumId = args.get(2);
        String username = args.get(3);
        String url = args.get(4);

        String ownerUserName = server.getUserNameBySessionID(sessionId);
        Album album;

        if (ownerUserName == null) {
            displayDebug(NOK4);
            return "NOK 4";
        } else {
            album = server.getAlbumById(new Integer(albumId));
            //Checks if album ID exists
            if (album == null) {
                displayDebug(NOK5);
                return "NOK 5";
            }

            //Checks if username exists
            if ((!server.usernameExists(username)) || (username.equals(ownerUserName))) {
                displayDebug(NOK6, username);
                return "NOK 6";
            }

            //URL validation
            if(!url.matches("\\b((http|https):\\/\\/?)[^\\s()<>]+(?:\\([\\w\\d]+\\)|([^[:punct:]\\s]|\\/?))")) {
                displayDebug(NOK7, url);
                return "NOK 7";
            }

            album.addUserPermission(username, url);
            displayDebug("** ALB-AUP: User " + ownerUserName + " added " + username + " to album " + albumId + " - '" + album.getTitle() + "'");
            return "OK";

        }
    }
}
