package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.activities.ChooseCloudOrLocal.wifiConnector;
import static pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector.WiFiDP2PhotoOperation.GET_CATALOG;

public class WiFiDFetchAllPendingCatalogs extends AsyncTask<Object, Object, String> {

    @Override
    protected String doInBackground(Object... params) {
        List<Integer> listOfIds = (List<Integer>) params[0];

        //Fetch all catalogs from all pending albums
        for (Integer id : listOfIds) {
            try {
                //Who is the owner of this pending album?
                String owner = Main.sv.getAlbumOwner(id);

                //Where is the owner?
                String ownerIP = wifiConnector.getArpCache().resolve(owner);

                if (ownerIP != null) {
                    wifiConnector.requestP2PhotoOperation(GET_CATALOG, ownerIP, Main.username, String.valueOf(id));
                }
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
