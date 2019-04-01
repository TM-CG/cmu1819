package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class CreateAlbum extends Instruction {

    CreateAlbum(List<String> args, Server server) {
        super("ALB-CR8", args, server);
    }

    @Override
    public String execute() {
        try {

            if (args.size() != 3)
                return ERR;

            String sessionId = args.get(1);
            String albumTitle = args.get(2);
            User owner = server.getUserByUsername(server.getUserNameBySessionID(sessionId));

            if (sessionId == null || albumTitle == null || albumTitle.contains("\""))
                return ERR;

            if (owner == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {

                String ownerURL = owner.getCloudURL() + "/" + albumTitle.toLowerCase() + "_" + Album.CounterID + ".alb";
                server.addAlbum(new Album(Album.CounterID, albumTitle, owner, ownerURL));

                displayDebug("User " + owner.getUsername() + " just created one album with title: '" + albumTitle + "'");
                return OK_PLUS + Album.CounterID++;
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
