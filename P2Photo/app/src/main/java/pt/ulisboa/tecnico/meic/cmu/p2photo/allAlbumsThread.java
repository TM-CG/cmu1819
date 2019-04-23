package pt.ulisboa.tecnico.meic.cmu.p2photo;


import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class allAlbumsThread implements Runnable {
    @Override
    public void run() {
        try {
            Cache.getInstance().ownedAndPartAlbumsIDs = MainActivity.getSv().listUserAlbums(ServerConnector.ListAlbumOption.VIEW_ALL);
        } catch (P2PhotoException e) {
            //TODO
        }
    }
}
