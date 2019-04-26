package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class CreateAlbumOnServer extends AsyncTask<Object,Object,Object[]> {

    private ServerConnector sv = Main.sv;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object[] o) {
        EditText album = (EditText) o[0];
        Context context = (Context) o[1];
        Integer albumId = null;
        ArrayList<String> items2 = null;

        if (o != null) {
            albumId = (Integer) o[2];
        } else {
            Log.i("CreateAlbumOnServer", "albumId is null");
            return;
        }

        String albumTitle = album.getText().toString();

        //create album catalog for new album
        AlbumCatalog catalog = new AlbumCatalog(albumId, albumTitle);

        Thread t1 = new Thread(new CloudStorage(context, catalog, StorageProvider.Operation.WRITE), "WritingThread");
        t1.start();
        try {
            t1.join();
            //create a new folder
            String albumName = albumId + " " + albumTitle;
            new CreateFolderTask().execute(albumName, context);
            String[] splited = albumName.split(" ");
            Cache.getInstance().albumsIDs.add(Integer.parseInt(splited[0]));
            Cache.getInstance().albums.add(splited[1]);
            Cache.getInstance().ownedAndPartAlbumsIDs.add(Integer.parseInt(splited[0]));
            Cache.getInstance().ownedAndPartAlbums.add(splited[1]);
            Cache.getInstance().ownedAlbumsIDs.add(Integer.parseInt(splited[0]));
            Cache.getInstance().ownedAlbums.add(splited[1]);
            Cache.getInstance().ownedAlbumWithIDs.add(splited[0] + " " + splited[1]);
            //add users to albums
            new AddUsersToAlbum().execute(o);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected Object[] doInBackground(Object[] objects) {
        Object[] result = new Object[2];
        //adds the array of users (names)
        result[0] = objects[0];
        try {
            result[1] = sv.createAlbum();
            return result;
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }

        return null;
    }
}
