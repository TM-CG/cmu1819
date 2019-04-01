package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class UpdateAlbum extends Instruction {

    UpdateAlbum(List<String> args, Server server) {
        super("ALB-AUP", args, server);
    }

    @Override
    public String execute() {

        try {

            if (args.size() != 4)
                return "ERR";

            String sessionId = args.get(1);
            String albumId = args.get(2);
            String username = args.get(3);

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

                //Checks if username exists, if the user is not adding himself to the album in which is owner
                //or changing another album which is not the owner
                if ((!server.usernameExists(username)) || (username.equals(ownerUserName)) ||
                        (!ownerUserName.equals(album.getOwner()))) {
                    displayDebug(NOK6, username);
                    return "NOK 6";
                }

                String url = server.getUserByUsername(username).getCloudURL() + "/" +
                        server.getAlbumById(Integer.parseInt(albumId)).getTitle().toLowerCase() + "_" + albumId + ".alb";

                album.addUserPermission(username, url);
                displayDebug("** ALB-AUP: User " + ownerUserName + " added " + username + " to album " + albumId + " - '" + album.getTitle() + "'");
                return "OK " + albumId;

            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return "ERR";
        }
    }
}
