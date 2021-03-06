package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class CreateAlbum extends Instruction {

    CreateAlbum(List<String> args, Server server) {
        super("ALB-CR8", args, server);
    }

    @Override
    public String execute() {
        try {

            if (args.size() < 2 || args.size() > 3)
                return ERR;

            String sessionId = args.get(1);
            String albumDirectoryURL = null;
            if (args.size() == 3)
                albumDirectoryURL = args.get(2);
            User owner = server.getUserByUsername(server.getUserNameBySessionID(sessionId));

            if (sessionId == null)
                return ERR;

            if (owner == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {
                if (albumDirectoryURL == null)
                    server.addAlbum(new Album(Server.CounterID, owner));
                else server.addAlbum(new Album(Server.CounterID, owner, albumDirectoryURL));

                displayDebug("User %s just created one album with ID %d ", owner.getUsername(), Server.CounterID);
                return OK_PLUS + Server.CounterID++;
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
