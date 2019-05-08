package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;

public class LocalCacheInit extends AsyncTask<Object, String, String> {

    private static final String TAG = LocalCacheInit.class.getName();

    @Override
    protected String doInBackground(Object... params) {
        File folder = new File((String) params[0]);
        Cache cacheInstance = (Cache) params[1];
        Log.d(TAG, "Started LocalCacheInit");

        File[] files = folder.listFiles();
        BufferedReader br;
        String line;

        for (File file : files) {
            if (file.getName().endsWith("_catalog.txt")) {
                try {
                    Log.d(TAG, "Found " + file.getName());
                    br = new BufferedReader(new FileReader(file));
                    line = br.readLine();

                    synchronized (cacheInstance) {
                        String[] splited = line.split(" ");

                        if (!cacheInstance.albums.contains(splited[1])) {
                            Log.d(TAG, file.getName() + " first line: " + splited[0] + " " + splited[1]);

                            cacheInstance.albumsIDs.add(Integer.parseInt(splited[0]));
                            cacheInstance.albums.add(splited[1]);
                            //add to owned
                            if (cacheInstance.ownedAlbumsIDs.contains(Integer.parseInt(splited[0]))) {
                                cacheInstance.ownedAlbums.add(splited[1]);
                            }
                            //add to owned and parsed
                            else if (cacheInstance.ownedAndPartAlbumsIDs.contains(Integer.parseInt(splited[0]))) {


                            }
                            cacheInstance.ownedAndPartAlbums.add(splited[1]);
                            cacheInstance.ownedAlbumWithIDs.add(splited[0] + " " + splited[1]); //same but parsed

                        }
                        Log.d(TAG, "Cache Size: " + Cache.ownedAlbumWithIDs.size());
                        cacheInstance.notifyAdapters();
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

        return null;
    }
}
