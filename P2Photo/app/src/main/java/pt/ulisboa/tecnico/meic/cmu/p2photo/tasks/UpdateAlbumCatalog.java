package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage.CATALOG_SUFFIX;

public class UpdateAlbumCatalog extends AsyncTask {
    public static final String TAG = "UpdateAlbumCatalog";

    @Override
    protected Object doInBackground(Object[] objects) {
        File catalogFile = (File) objects[0];

        Integer albumId = (Integer) objects[1];
        Log.i(TAG, "UpdateAlbumCatalog: AlbumID: " + albumId);
        String photoURL = (String) objects[2];
        Log.i(TAG, "UpdateAlbumCatalog: photoURL: " + photoURL);
        String fileName = String.format(CATALOG_SUFFIX, albumId);


        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        if (catalogFile != null) {
            //append new photoURL
            try {
                fileWriter = new FileWriter(catalogFile.getAbsoluteFile(), true);
                bufferedWriter = new BufferedWriter(fileWriter);
                Log.i(TAG, "UpdateAlbumCatalog: Writing URL to catalog: " + photoURL);
                bufferedWriter.write(photoURL + "\n");

            } catch (IOException e) {
                Log.i(TAG, "UpdateAlbumCatalog: IOException");
            } finally {

                try {

                    if (bufferedWriter != null)
                        bufferedWriter.close();

                    if (fileWriter != null)
                        fileWriter.close();

                } catch (IOException ex) {

                    Log.i(TAG, "UpdateAlbumCatalog: IOException when closing");

                }
            }
        }
        else {
            Log.i(TAG, "UpdateAlbumCatalog: catalogFile is null!");
        }

        if (catalogFile == null)
            return null;
        return catalogFile.getAbsolutePath();
    }
}