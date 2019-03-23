package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class CreateAlbum extends Instruction {

    CreateAlbum(List<String> args, Server server) {
        super("ALB-CR8", args, server);
    }

    @Override
    public String execute() {
        String sessionId = args.get(1);
        String albumTitle = args.get(2);
        User owner = server.getUserByUsername(server.getUserNameBySessionID(sessionId));

        if (owner == null) {
            displayDebug(NOK4);
            return "NOK 4";
        }
        else {

            server.addAlbum(new Album(Album.CounterID, albumTitle, owner));

            displayDebug("User " + owner.getUsername() + " just created one album with title: '" + albumTitle + "'");
            return "OK " + Album.CounterID++;
        }
    }
}
