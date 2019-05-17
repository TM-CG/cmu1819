package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class DisplayAlbumKey extends Instruction {

    public DisplayAlbumKey(List<String> args, Server server) {
        super("ALB-DAK", args, server);
    }

    @Override
    public String execute() {

        try {
            if (args.size() != 3)
                return ERR;

            String sessionId = args.get(1);
            String albumId = args.get(2);
            String username = server.getUserNameBySessionID(sessionId);

            if (username == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            }

            Album album = server.getAlbumById(new Integer(albumId));
            if (album == null) {
                displayDebug(VERBOSE_NOK5);
                return NOK_5;
            }

            String indexOfUser = album.getIndexOfUser(username);

            if (album != null && album.equals("NA")) {
                displayDebug(VERBOSE_NOK5);
                return NOK_5;
            }

            return OK_PLUS + album.getAlbumKeyOfUser(username);


        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
