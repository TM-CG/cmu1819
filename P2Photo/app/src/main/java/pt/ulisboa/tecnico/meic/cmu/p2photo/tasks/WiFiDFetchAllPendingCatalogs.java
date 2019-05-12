package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import java.util.List;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.activities.ChooseCloudOrLocal.wifiConnector;
import static pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector.WiFiDP2PhotoOperation.GET_CATALOG;

public class WiFiDFetchAllPendingCatalogs extends AsyncTask<Object, Object, String> {

    @Override
    protected String doInBackground(Object... params) {
        List<Integer> listOfIds = (List<Integer>) params[0];

        //Fetch all catalogs from all pending albums
        for (Integer id : listOfIds)
            wifiConnector.requestP2PhotoOperation(GET_CATALOG, String.valueOf(id));
        return null;
    }
}
