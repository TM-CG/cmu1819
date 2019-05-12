package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.LocalStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.CreateFolder;
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
            new GetAlbumURL().execute(albumNametv, Integer.parseInt(id)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        albumIDtv.setText(id);
        cacheInstance.loadingSpinner(false);
    }

    public void acceptInvitation(View view) {
        Cache.getInstance().progressBar = (ProgressBar) findViewById(R.id.loading);
        Cache.getInstance().loadingSpinner(true);
        name = albumNametv.getText().toString();
        AlbumCatalog catalog = new AlbumCatalog(Integer.parseInt(id), name);
        Thread t1 = null;

        if (Main.STORAGE_TYPE == Main.StorageType.CLOUD)
            t1 = new Thread(new CloudStorage(getApplicationContext(), catalog, StorageProvider.Operation.WRITE), "WritingThread");
        else if (Main.STORAGE_TYPE == Main.StorageType.LOCAL)
            t1 = new Thread(new LocalStorage(getApplicationContext(), catalog, StorageProvider.Operation.WRITE), "WritingThread");

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String albumDescription = id + " " + name;
        new CreateFolder().execute(albumDescription, getApplicationContext());
        Cache.getInstance().albumsIDs.add(Integer.parseInt(id));
        Cache.getInstance().albums.add(name);
        Cache.getInstance().ownedAndPartAlbumsIDs.add(Integer.parseInt(id));
        Cache.getInstance().ownedAndPartAlbums.add(name);
        Cache.getInstance().ownedAlbumWithIDs.add(albumDescription);
        Cache.getInstance().notifyAdapters();
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        Cache.getInstance().clientLog.add(Main.username + " accepted invitation from " + ownerNametv.getText().toString() +  "to participade in album " + name + " at "  + new Timestamp(System.currentTimeMillis()));

        finish();
    }

    public void rejectInvitation(View view) {
        new RejectPendingInvitation().execute(id);
        Cache.getInstance().clientLog.add(Main.username + " rejected invitation to participade in album with id " + id  +  " at " + new Timestamp(System.currentTimeMillis()));
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

}
