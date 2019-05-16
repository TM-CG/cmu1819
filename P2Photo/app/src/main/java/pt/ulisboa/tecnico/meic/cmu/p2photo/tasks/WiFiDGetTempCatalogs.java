package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector;

/**
 * Async Task for downloading temporary catalog to be parsed one time.
 */
public class WiFiDGetTempCatalogs extends AsyncTask<Object, String, String> {
    private static final String TAG = WiFiDGetTempCatalogs.class.getName();

    @Override
    protected String doInBackground(Object... params) {
        Integer album = (Integer) params[0];
        WiFiDConnector wiFiDConnector = (WiFiDConnector) params[1];

        try {
            List<String> participants = Main.sv.listUserAlbumSlices(album);
            String ip;

            for (String username : participants) {
                if (!username.equals(Main.username)) {
                    ip = wiFiDConnector.getArpCache().resolve(username);
                    if (ip != null) {
                        Log.d(TAG, String.format("Send temporary get catalog request to %s at %s", username, ip));
                        wiFiDConnector.requestP2PhotoOperation(WiFiDConnector.WiFiDP2PhotoOperation.GET_CATALOG, ip, Main.username,
                                String.valueOf(album), "T");
                    }
                }

            }

        } catch (P2PhotoException e) {
            e.printStackTrace();
        }


        return null;
    }
}
