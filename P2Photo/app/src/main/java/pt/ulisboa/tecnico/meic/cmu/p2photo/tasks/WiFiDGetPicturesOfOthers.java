package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.ListPhotoAdapter;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector;

/**
 * Async Task to retrieve all files from other Wi-Fi pictures
 * We contact the server to obtain the username of all participants
 * We check if the participants are near by and request their pictures
 */
public class WiFiDGetPicturesOfOthers extends AsyncTask<Object, String, String> {
    private static final String TAG = WiFiDGetPicturesOfOthers.class.getName();

    @Override
    protected String doInBackground(Object... parms) {
        try {
            Integer album = (Integer) parms[0];
            String albumTitle = (String) parms[1];
            WiFiDConnector wiFiDConnector = (WiFiDConnector) parms[2];

            String albumFolder = album + " " + albumTitle;

            //Get participants of this album
            List<String> participants = Main.sv.listUserAlbumSlices(album);
            String ip;

            for (String username : participants) {
                if (!username.equals(Main.username)) {
                    ip = wiFiDConnector.getArpCache().resolve(username);
                    if (ip != null) //user is reachable
                    {
                        //Parse catalog to object
                        String path2File = Main.CACHE_FOLDER + "/" + Main.username + "/" + "tmp/" +
                                username + ".txt";
                        //Read Catalog file
                        File catalog = new File(path2File);

                        if (!catalog.exists())
                            return null;

                        try {
                            FileReader reader = new FileReader(catalog);
                            BufferedReader bufferedReader = new BufferedReader(reader);

                            String line, content = "";

                            while ((line = bufferedReader.readLine()) != null) {
                                content += line + "\n";
                            }

                            bufferedReader.close();
                            AlbumCatalog receivedCatalog = AlbumCatalog.parseToAlbumCatalog(content);
                            List<String> picturesFilesNames = receivedCatalog.getPaths2Pics();

                            for (String pictureFileName : picturesFilesNames) {
                                //Request to receive every picture one by one
                                Log.d(TAG, String.format("Request to %s to download picture at path %s", ip, albumFolder + "/" + pictureFileName));
                                wiFiDConnector.requestP2PhotoOperation(WiFiDConnector.WiFiDP2PhotoOperation.GET_PICTURE, ip, Main.username, albumFolder + "/" + pictureFileName);
                            }


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        } catch (P2PhotoException e) {
            e.printStackTrace();
        }
        return null;

    }
}
