package pt.ulisboa.tecnico.meic.cmu.p2photo;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class owningAlbumsThread extends Thread {
    @Override
    public void run() {
        try {
            Cache.getInstance().ownedAlbumsIDs = MainActivity.getSv().listUserAlbums(ServerConnector.ListAlbumOption.VIEW_OWN);
        } catch (P2PhotoException e) {
            //TODO
        }
    }
}
