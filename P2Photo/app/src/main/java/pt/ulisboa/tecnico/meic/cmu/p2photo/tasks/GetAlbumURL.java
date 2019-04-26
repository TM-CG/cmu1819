package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
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
    protected void onPostExecute(Object[] o) {
        if (o != null) {
            BufferedReader br = null;
            TextView tv = (TextView) o[0];
            File f = (File) o[1];

            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                tv.setText(br.readLine().split(" ")[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

        @Override
        protected Object[] doInBackground(Object[] objects) {
            Integer albumID = (Integer) objects[1];
            Context context = (Context) objects[2];
            Object[] result = new Object[2];
            result[0] = objects[0];
            try {
                List<String> urlList = sv.listUserAlbumSlices(albumID);
                if (urlList.size() > 0) {
                    String ownerURL = urlList.get(0);
                    try {
                        File path;
                        path = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS + "/" + Main.username);
                        File file = new File(path, "albumName.txt");

                        // Download the file.
                        try (OutputStream outputStream = new FileOutputStream(file)) {
                            DropboxClientFactory.getClient().sharing().getSharedLinkFile(ownerURL).download(outputStream);
                        }
                        result[1] = file;
                        return result;
                    } catch (DbxException | IOException e) {
                    }
                }
                return null;
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }
            return null;

        }
}
