package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class AddAlbumSliceCatalogURL extends AsyncTask {

    private static final String TAG = AddAlbumSliceCatalogURL.class.getName();
    private ServerConnector sv = Main.sv;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {

    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.i(TAG, "Starting doInBackground");
        try {
            int albumId = (int) objects[0];
            String sliceURL = (String) objects[1];

            sv.acceptIncomingRequest(albumId, sliceURL);
            Log.i(TAG, "Accepted my own request!");

        } catch (P2PhotoException e) {
            e.printStackTrace();
        }

        return null;
    }
}
