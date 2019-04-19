package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class ActionOnPendingActivity extends AppCompatActivity {
    private TextView albumIDtv;
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
        albumNametv = (TextView) findViewById(R.id.pendingTittle);
        Intent intent = getIntent();
        id = intent.getStringExtra("albumID");
        position = cacheInstance.albumsIDs.indexOf(Integer.parseInt(id));
        name = cacheInstance.albums.get(position);
        albumIDtv.setText(id);
        albumNametv.setText(name);
    }

    public void acceptInvitation(View view) {
        try {
            new processRequest().execute("OK", id, getApplicationContext()).get();
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);
            finish();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void rejectInvitation(View view) {
        try {
            new processRequest().execute("NOK", id, getApplicationContext()).get();
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);
            finish();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

