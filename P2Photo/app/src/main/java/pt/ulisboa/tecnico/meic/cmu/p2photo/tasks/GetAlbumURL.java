package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.dropbox.core.DbxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class GetAlbumURL extends AsyncTask<Object, Object, Object[]> {
    private ServerConnector sv = Main.sv;

        @Override
        protected Object[] doInBackground(Object[] objects) {
            Integer albumID = (Integer) objects[1];
            Object[] result = new Object[2];
            result[0] = objects[0];

            TextView tv = (TextView) objects[0];
            try {
                List<String> urlList = sv.listUserAlbumSlices(albumID);
                if (urlList.size() > 0) {
                    String ownerURL = urlList.get(0);

                    Log.i("GetAlbumURL", "Link received from server: " + ownerURL);

                    try {
                        File path;
                        path = Environment.getExternalStoragePublicDirectory(
                                Main.CACHE_FOLDER + "/" + Main.username);

                        //Create tmp folder if not exists
                        path.mkdir();

                        File file = new File(path, "tmp_catalog.txt");


                        // Download the file.
                        try (OutputStream outputStream = new FileOutputStream(file)) {
                            DropboxClientFactory.getClient().sharing().getSharedLinkFile(ownerURL).download(outputStream);
                        }


                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                        String albumTitle = bufferedReader.readLine().split(" ")[1];
                        Log.i("GetAlbumURL", "Album title: " + albumTitle);
                        tv.setText(albumTitle);

                        result[1] = file;
                        return result;
                    } catch (DbxException | IOException e) {
                        Log.i("GetAlbumURL", "Got an exception : " + e.getMessage());

                    }
                }
                return null;
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }
            return null;

        }
}
