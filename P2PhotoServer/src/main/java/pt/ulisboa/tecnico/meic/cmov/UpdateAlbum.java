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
                return ERR;

            String sessionId = args.get(1);
            String albumId = args.get(2);
            String username = args.get(3);

            String ownerUserName = server.getUserNameBySessionID(sessionId);
            Album album;

            if (ownerUserName == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {

                album = server.getAlbumById(Integer.parseInt(albumId));
                //Checks if album ID exists
                if (album == null) {
                    displayDebug(VERBOSE_NOK5);
                    return NOK_5;
                }

                //Checks if username exists, if the user is not adding himself to the album in which is owner
                //or changing another album which is not the owner
                if ((!server.usernameExists(username)) || (username.equals(ownerUserName)) ||
                        (!ownerUserName.equals(album.getOwner()))) {
                    displayDebug(VERBOSE_NOK6, username);
                    return NOK_6;
                }

                album.addUserPermission(username, null);
                displayDebug("User %s added %s to album %s", ownerUserName, username, albumId);
                return OK_PLUS + albumId;

            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
	        return ERR;
        }
    }
}
