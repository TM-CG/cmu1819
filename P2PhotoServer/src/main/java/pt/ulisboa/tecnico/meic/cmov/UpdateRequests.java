package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

public class UpdateRequests extends Instruction {

    UpdateRequests(List<String> args, Server server) {
        super("USR-URQ", args, server);
    }

    @Override
    public String execute() {
        try {

            if (args.size() < 3)
                return ERR;

            String sessionId = args.get(1);
            String option = args.get(2);
            int albumId = Integer.parseInt(args.get(3));
            String directoryCloudURL = null;

            if (args.size() > 4)
                directoryCloudURL = args.get(4);

            String username = server.getUserNameBySessionID(sessionId);

            if (username == null) {
                displayDebug(VERBOSE_NOK4);
                return NOK_4;
            } else {
                //user accepted invitation
                if (option.equals("A")) {
                    server.getAlbumById(albumId).setIndexOfParticipant(username, directoryCloudURL);
                } else {
                    //user reject invitation
                    server.getAlbumById(albumId).removeIndexOfParticipant(username);
                }

                return OK_PLUS + albumId;

            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            return ERR;
        }
    }
}
