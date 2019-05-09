package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.util.Log;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class OwningAlbums implements Runnable {
    @Override
    public void run() {
        try {
            synchronized (Cache.getInstance()) {
                Cache.getInstance().ownedAlbumsIDs = Main.getSv().listUserAlbums(ServerConnector.ListAlbumOption.VIEW_OWN);
                Log.d("OwningAlbums", "Size: " + Cache.getInstance().ownedAlbumsIDs.size());
            }
        } catch (P2PhotoException e) {
            //TODO
        }
    }
}
