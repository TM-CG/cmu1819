package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AllAlbums;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.DownloadFile;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.ListFolder;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.OwningAlbums;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.GetCurrentAccount;


public class ChooseCloudOrLocal extends P2PhotoActivity {
    private static final String TAG = ChooseCloudOrLocal.class.getName();

    private Cache cacheInstance = Cache.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Just to clean old broadcast receiver of WiFi Direct
        if (wifiConnector != null) {
            wifiConnector.unRegisterReceiver();
            wifiConnector = null;
        }
        //Storage is not available at this moment!
        Main.STORAGE_TYPE = Main.StorageType.NA;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cloud_local);
    }

    public void goBack(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void selectAuthCloud(View view){
        Auth.startOAuth2Authentication(ChooseCloudOrLocal.this, getString(R.string.app_key));

        Button cloudButton = (Button) findViewById(R.id.cloudButton);
        //cloudButton.setEnabled(true);
    }

    public void selectCloud(View view){
        Main.STORAGE_TYPE = Main.StorageType.CLOUD;
        Intent intent = new Intent(this, ActionsMenu.class);
        cacheInstance.cleanArrays();
        loadCache();
        startActivityForResult(intent, 4);
    }

    public void selectLocalWiFi(View view) {
        Main.STORAGE_TYPE = Main.StorageType.LOCAL;
        //wifiConnector = new WiFiDConnector(this, Main.sv);
        Log.i(TAG, "Already constructed Wi-Fi direct object!");

        //loadLocalCache();

        Intent intent = new Intent(this, ActionsMenu.class);
        startActivityForResult(intent, 4);
    }

    public void debugSendMessage(View view) {
        //wifiConnector.sendMessage("Eureka", WiFiDConnector.MsgType.TEXT);
        wifiConnector.sendFile("pasta do vitor", "/sdcard/Download/download.jpg");
    }

    @Override
    protected void loadData() {
        new GetCurrentAccount(DropboxClientFactory.getClient(), new GetCurrentAccount.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                Log.i("DROPBOX", result.getEmail());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Choose cloud option*/
            case 3:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;
            /*logOut*/
            case 4:
                if(resultCode==RESULT_OK){
                    Intent intent = getIntent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else if(resultCode==RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }
    
    protected void loadCache() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading cache");
        dialog.show();



        new ListFolder(DropboxClientFactory.getClient(), new ListFolder.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                dialog.dismiss();
                if(result != null) {
                    try {
                        Log.d("entradas", result.getEntries().toString());
                        //get my albums
                        Thread t1 = new Thread(new AllAlbums());
                        t1.start();
                        Thread t2 = new Thread(new OwningAlbums());
                        t2.start();
                        t1.join();
                        t2.join();
                        Log.d("ownedAlbums", "owned and part");
                        Log.d("ownedAlbums", String.valueOf(cacheInstance.ownedAndPartAlbumsIDs.size()));
                        for(Integer elm : cacheInstance.ownedAndPartAlbumsIDs){
                            Log.d("ownedAlbums", String.valueOf(elm));
                        }
                        Log.d("ownedAlbums", "owned");
                        Log.d("ownedAlbums", String.valueOf(cacheInstance.ownedAlbumsIDs.size()));
                        for(Integer elm : cacheInstance.ownedAlbumsIDs){
                            Log.d("ownedAlbums", String.valueOf(elm));
                        }
                        for(String elm:cacheInstance.ownedAlbumWithIDs ) {
                            Log.d("ownedAlbums", elm);
                        }
                        for(Metadata m : result.getEntries()){
                            Log.d("entrei", "estou nas metadatas");
                            if(m.getName().endsWith("_catalog.txt")) {
                                Log.d("entrei", "sou um catalog");
                                downloadFile((FileMetadata) m);
                            }
                        }

                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();


            }
        }).execute("/" + Main.username);

    }

    private void downloadFile(FileMetadata file) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Reading catalogs");
        dialog.show();

        new DownloadFile(this, DropboxClientFactory.getClient(), new DownloadFile.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                dialog.dismiss();
                if(result != null) {
                    try {
                        Log.i("chooseCloudLocalActivit", "Im on download!");
                        BufferedReader br = new BufferedReader(new FileReader(result));
                        String st;
                        while ((st = br.readLine()) != null) {
                            synchronized (cacheInstance) {
                                String[] splited = st.split(" ");
                                if (!cacheInstance.albums.contains(splited[1])) {
                                    //all results contained in dropbox
                                    cacheInstance.albumsIDs.add(Integer.parseInt(splited[0]));
                                    cacheInstance.albums.add(splited[1]);
                                    //add to owned
                                    if (cacheInstance.ownedAlbumsIDs.contains(Integer.parseInt(splited[0]))) {
                                        cacheInstance.ownedAlbums.add(splited[1]);
                                        cacheInstance.ownedAndPartAlbums.add(splited[1]);
                                        cacheInstance.ownedAlbumWithIDs.add(splited[0] + " " + splited[1]); //same but parsed
                                    }
                                    //add to owned and parsed
                                    else if (cacheInstance.ownedAndPartAlbumsIDs.contains(Integer.parseInt(splited[0]))) {
                                        cacheInstance.ownedAndPartAlbums.add(splited[1]);
                                        cacheInstance.ownedAlbumWithIDs.add(splited[0] + " " + splited[1]); //same but parsed

                                    }


                                }
                                cacheInstance.notifyAdapters();
                            }
                            break;
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Log.i("download", "fail");

            }
        }).execute(file);

    }
}
