package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

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
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDGetTitleOfPending;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.activities.ChooseCloudOrLocal.wifiConnector;
import static pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector.WiFiDP2PhotoOperation.GET_CATALOG;

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
            if (Main.STORAGE_TYPE == Main.StorageType.CLOUD) {
                new GetAlbumURL().execute(albumNametv, Integer.parseInt(id)).get();
            }
            else if (Main.STORAGE_TYPE == Main.StorageType.LOCAL) {
                //I've already received all catalogs from near by devices so for this one let me get the title
                new WiFiDGetTitleOfPending().execute(id, albumNametv);
            }
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
        finish();
    }

    public void rejectInvitation(View view) {
        new RejectPendingInvitation().execute(id);
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

}
