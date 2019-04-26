package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.CreateFolderTask;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.GetAlbumOwner;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.GetAlbumURL;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.RejectPendingInvitation;

public class ActionOnPending extends AppCompatActivity {
    private TextView albumIDtv;
    private TextView ownerNametv;
    private TextView albumNametv;
    private String id;
    private String name = null;
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
        new GetAlbumOwner().execute(ownerNametv,id);
        try {
            new GetAlbumURL().execute(albumNametv, Integer.parseInt(id), getApplicationContext()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        albumIDtv.setText(id);
    }

    public void acceptInvitation(View view) {
        name = albumNametv.getText().toString();
        AlbumCatalog catalog = new AlbumCatalog(Integer.parseInt(id), name);
        Thread t1 = new Thread(new CloudStorage(getApplicationContext(), catalog, StorageProvider.Operation.WRITE), "WritingThread");
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String albumName = id + " " + name;
        new CreateFolderTask().execute(albumName, getApplicationContext());
        String[] splited = albumName.split(" ");
        Cache.getInstance().albumsIDs.add(Integer.parseInt(splited[0]));
        Cache.getInstance().albums.add(splited[1]);
        Cache.getInstance().ownedAndPartAlbumsIDs.add(Integer.parseInt(splited[0]));
        Cache.getInstance().ownedAndPartAlbums.add(splited[1]);
        Cache.getInstance().ownedAlbumWithIDs.add(splited[0] + " " + splited[1]);
        Cache.getInstance().notifyAdapters();
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void rejectInvitation(View view) {
        new RejectPendingInvitation().execute(id);
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

}
