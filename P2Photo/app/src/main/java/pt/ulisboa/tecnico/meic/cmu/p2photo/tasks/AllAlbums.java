package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;


import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class AllAlbums implements Runnable {
    @Override
    public void run() {
        try {
            Cache.getInstance().ownedAndPartAlbumsIDs = Main.getSv().listUserAlbums(ServerConnector.ListAlbumOption.VIEW_ALL);
        } catch (P2PhotoException e) {
            //TODO
        }
    }
}
