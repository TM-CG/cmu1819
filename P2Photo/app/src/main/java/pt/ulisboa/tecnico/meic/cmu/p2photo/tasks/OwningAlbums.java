package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class OwningAlbums implements Runnable {
    @Override
    public void run() {
        try {
            Cache.getInstance().ownedAlbumsIDs = Main.getSv().listUserAlbums(ServerConnector.ListAlbumOption.VIEW_OWN);
        } catch (P2PhotoException e) {
            //TODO
        }
    }
}
