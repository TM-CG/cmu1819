package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;

public class WiFiDGetTitleOfPending extends AsyncTask<Object, String, String> {

    @Override
    protected String doInBackground(Object... params) {
        String id = (String) params[0];
        TextView albumNametv = (TextView) params[1];

        String path2File = Main.CACHE_FOLDER + "/" + Main.username + "/" +
                id + "_catalog.txt";
        //Read Catalog file
        File catalog = new File(path2File);

        if (!catalog.exists())
            return null;

        try {
            FileReader reader = new FileReader(catalog);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String firstLine = bufferedReader.readLine();
            String albumTitle = firstLine.substring(firstLine.indexOf(' ') + 1);

            albumNametv.setText(albumTitle);
            return albumTitle;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
