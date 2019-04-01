package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.util.List;

public class ListAlbum extends Instruction {

    ListAlbum(List<String> args, Server server) {
        super("ALB-LST", args, server);
    }

    @Override
    public String execute() {
        String sessionId = args.get(1);
        String username = server.getUserNameBySessionID(sessionId);

        if (username == null) {
            displayDebug(VERBOSE_NOK4);
            return NOK_4;
        } else {
            List<Pair<String,String>> albums = server.getAlbunsOfGivenUser(username);

            return OK_PLUS + server.representAlbum(albums);

        }
    }
}
