package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.util.List;

/**
 * Debug operation to display the status of the server
 */
public class DebugStatus extends Instruction {

    DebugStatus(List<String> args, Server server) {
        super("DBG-STA", args, server);
    }

    @Override
    public String execute() {

        System.out.println("============ BEGIN DEBUG ============");
        System.out.println("List of users: ");
        for (User user : server.getUsers()) {
            System.out.printf("Username: %s\tCloud URL:%s\n", user.getUsername(), user.getCloudURL());
        }

        System.out.println();
        System.out.println("List of user with active sessions: ");
        for (Pair<String, String> pair : server.getLoggedInUsers()) {
            System.out.printf("Username: %s\tSessionId: %s\n", pair.getKey(), pair.getValue());
        }

        System.out.println();
        System.out.println("List of albums: ");
        for (Album album : server.getAlbums()) {
            System.out.printf("ID: %d\tTitle: %s\tOwner: %s\n", album.getID(), album.getTitle(), album.getOwner());
        }
        System.out.println("============ END   DEBUG ============");

        return "OK";
    }
}
