package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class ListAlbum extends Instruction {

    /** View options **/
    static final String VIEW_ALL = "ALL";
    static final String VIEW_PAR = "PAR";
    static final String VIEW_OWN = "OWN";

    ListAlbum(List<String> args, Server server) {
        super("ALB-LST", args, server);
    }

    @Override
    public String execute() {
        try {

            if ((args.size() < 2) || (args.size() > 3))
                return ERR;

            String sessionId = args.get(1);
            String username = server.getUserNameBySessionID(sessionId);
            String option;

            //by default if option is not set display all albums (where the user is owner or not)
            if (args.size() == 2) {
                option = VIEW_ALL;
            } else {
                option = args.get(2);
            }

            if (username == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {

                List<Integer> albums = null;

                switch (option) {
                    case VIEW_ALL: albums = server.getAllAlbunsRelatedToUser(username);          break;
                    case VIEW_PAR: albums = server.getAlbunsWhereGiveUserParticipates(username); break;
                    case VIEW_OWN: albums = server.getAlbunsOwnedByGivenUser(username);          break;
                }

                displayDebug("User %s requested a list album", username);
                return OK_PLUS + server.representIntegerList(albums);

            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
