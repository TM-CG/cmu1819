package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class CreateAlbumOnServer extends AsyncTask<Object,Object,Object[]> {

    private ServerConnector sv = Main.sv;
    private String albumTitle;
    private Context context;
    private Integer albumId;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object[] o) {


        //create album catalog for new album
        AlbumCatalog catalog = new AlbumCatalog(albumId, albumTitle);

        Thread t1 = new Thread(new CloudStorage(context, catalog, StorageProvider.Operation.WRITE, o), "WritingThread");
        t1.start();
    }

    @Override
    protected Object[] doInBackground(Object[] objects) {
        Object[] result = new Object[2];
        //just to pass the list of users to add to this album
        result[1] = objects[2];

        albumTitle = (String) objects[0];
        context = (Context) objects[1];


        try {
            albumId = sv.createAlbum();
            result[0] = albumId;
            return result;
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }

        return null;
    }

}
