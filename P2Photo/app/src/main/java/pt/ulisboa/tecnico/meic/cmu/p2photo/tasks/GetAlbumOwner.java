package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.widget.TextView;

import pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

/*
 * Async call to discover the owner of an album
 */
public class GetAlbumOwner extends AsyncTask<Object, Object, Object[]> {
    private ServerConnector sv = MainActivity.sv;

    @Override
    protected void onPostExecute(Object[] o) {
        if(o!=null){
            TextView tv = (TextView) o[0];
            String ownerName = (String) o[1];
            tv.setText(ownerName);
        }
    }

    @Override
    protected Object[] doInBackground(Object[] objects) {
        Integer albumID = Integer.parseInt((String)objects[1]);
        try {
            objects[1] = sv.getAlbumOwner(albumID);
            return objects;
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }
        return null;
    }
}
