package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dropbox.core.DbxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class ActionOnPendingActivity extends AppCompatActivity {
    private TextView albumIDtv;
    private TextView ownerNametv;
    private TextView albumNametv;
    private String id;
    private String name;
    private Cache cacheInstance;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_on_pending);
        cacheInstance = Cache.getInstance();
        albumIDtv = (TextView) findViewById(R.id.albumIDtext);
        ownerNametv = (TextView) findViewById(R.id.ownerNameText);
        albumNametv = (TextView) findViewById(R.id.pendingTittle);
        Intent intent = getIntent();
        id = intent.getStringExtra("albumID");
        position = cacheInstance.albumsIDs.indexOf(Integer.parseInt(id));
        new getOwner().execute(ownerNametv,id);
        new getAlbumURL().execute(albumNametv, Integer.parseInt(id), getApplicationContext());
        albumIDtv.setText(id);
    }

    public void acceptInvitation(View view) {
        new processRequest().execute("OK", id, getApplicationContext());
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void rejectInvitation(View view) {
        new processRequest().execute("NOK", id, getApplicationContext());
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

}
class processRequest extends AsyncTask<Object, Object, Object[]> {

    private ServerConnector sv = MainActivity.sv;

    @Override
    protected void onPostExecute(Object[] o) {

    }

    @Override
    protected Object[] doInBackground(Object[] objects) {
        String state = (String) objects[0];
        String id = (String) objects[1];
        Context context = (Context) objects[2];
        if (state.equals("OK")) {
            AlbumCatalog catalog = new AlbumCatalog(Integer.parseInt(id), "DEFAULT_VALUE");
            Thread t1 = new Thread(new CloudStorage(context, catalog, StorageProvider.Operation.WRITE), "WritingThread");
            t1.start();
        } else {
            try {
                sv.rejectIncomingRequest(Integer.parseInt(id));
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

class getOwner extends AsyncTask<Object, Object, Object[]> {

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

class getAlbumURL extends AsyncTask<Object, Object, Object[]> {

    private ServerConnector sv = MainActivity.sv;
    @Override
    protected void onPostExecute(Object[] o) {
        if(o!=null){
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
            if(urlList.size() > 0) {
                String ownerURL = urlList.get(0);
                try {
                    File path;
                    path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS + "/" + MainActivity.username);
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

/*class getAlbumURL extends AsyncTask<Object, Object, Object[]> {

    private ServerConnector sv = MainActivity.sv;
    @Override
    protected void onPostExecute(Object[] o) {
        if(o!=null){
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
            if(urlList.size() > 0) {
                String ownerURL = urlList.get(0);
                try {
                    File f = new DownloadFileFromLinkTask(context, DropboxClientFactory.getClient(), new DownloadFileFromLinkTask.Callback() {

                        @Override
                        public void onDownloadComplete(File result) {
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    }).execute(ownerURL,"","albumName").get();
                    result[1] = f;
                    return result;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }
        return null;
    }
}*/

