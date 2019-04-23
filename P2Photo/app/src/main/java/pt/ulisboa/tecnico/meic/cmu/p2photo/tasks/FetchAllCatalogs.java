package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class FetchAllCatalogs extends AsyncTask<Object, List<String>, List<String>> {
    private ServerConnector sv = MainActivity.sv;
    @Override
    protected List<String> doInBackground(Object [] objects) {
        try {
            int albumId = (Integer) objects[0];
            List<String> result;
            result = sv.listUserAlbumSlices(albumId);
            return result;

        } catch (P2PhotoException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<String> result) {

    }

}
