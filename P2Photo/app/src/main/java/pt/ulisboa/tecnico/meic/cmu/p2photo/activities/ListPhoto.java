package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.FilesAdapter;
import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.ListPhotoAdapter;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.DownloadCatalogsAndPhotos;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.DownloadFileFromLink;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.FetchAllCatalogs;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.LocalFileCopy;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDGetPicturesOfOthers;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDGetTempCatalogs;

public class ListPhoto extends P2PhotoActivity implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = ListPhoto.class.getName();

    private String catalogFile;
    private int albumId;
    private String albumTitle;
    private ListPhotoAdapter adapter;
    private TextView albumNametv;
    private String mPath;
    private FilesAdapter mFilesAdapter;
    private FileMetadata mSelectedFile;

    public static Intent getIntent(Context context, int albumId, String albumTitle) {
        Intent filesIntent = new Intent(context, ListPhoto.class);
        filesIntent.putExtra("album", albumId);
        filesIntent.putExtra("albumTitle", albumTitle);
        return filesIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo);
        Log.i(TAG, "Setted content view!");

        this.albumId = getIntent().getExtras().getInt("album");
        this.albumTitle = getIntent().getExtras().getString("albumTitle");
        albumNametv = (TextView) findViewById(R.id.grid_name);
        albumNametv.setText(albumTitle);
        Log.i(TAG, "Album: " + albumId);

        catalogFile = getIntent().getStringExtra("catalog");

        String tmpFolderPath = albumId + " "
                + albumTitle;


            //List<String> catalogsURL = new FetchAllCatalogs().execute(albumId).get();



            //Already fetch all catalogs from server now lets download it
            //Log.i(TAG, "Downloaded catalogs path from server " + catalogsURL.size());

            GridView gridView = (GridView) findViewById(R.id.grid_thumbnails);

            String path2Album = Main.CACHE_FOLDER + "/" + Main.username + "/" + tmpFolderPath;
            File userFolder = new File(getCacheDir(), Main.username);
            userFolder.mkdir();
            File folder = new File(userFolder, tmpFolderPath);
            folder.mkdir();

            Log.d(TAG, "Path2Album: " + folder.getAbsolutePath());
            adapter = new ListPhotoAdapter(this, folder);
            gridView.setAdapter(adapter);
            Cache.getInstance().loadingSpinner(false);

            if (Main.STORAGE_TYPE == Main.StorageType.CLOUD) {
                new DownloadCatalogsAndPhotos(this, adapter).execute(albumId, albumTitle);
            }


        //Define onclick to preview the photo using external app
        //GridView gridView = findViewById(R.id.grid_thumbnails);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse((String) imageView.getTag()), "image/*");
                startActivity(intent);
            }
        });

        if (Main.STORAGE_TYPE == Main.StorageType.LOCAL) {
            //Copy local slice to album
            localCopyCache(Main.username + "/" + tmpFolderPath);

            new WiFiDGetTempCatalogs(){
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    new WiFiDGetPicturesOfOthers(){
                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            Log.d(TAG, "Finishes download photos! Notifying adapter");
                            adapter.notifyDataSetChanged();
                        }
                    }.execute(albumId, albumTitle, ChooseCloudOrLocal.wifiConnector);
                }
            }.execute(albumId, ChooseCloudOrLocal.wifiConnector);

        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.addPhoto:
                intent = new Intent(this, AddPhoto.class);
                startActivityForResult(intent, 12);
                return true;
            case R.id.addUser:
                intent = new Intent(this, AddUser.class);
                startActivityForResult(intent, 13);
                return true;

        }
        return false;
    }

    /**
     * Copies a Local Album to the cache
     * @param folderName the local path to album folder
     */
    private void localCopyCache(String folderName) {
        File folder = new File(Main.DATA_FOLDER + "/" + folderName);
        File[] files = folder.listFiles();

        Log.d(TAG, "localCopyCache -> Local Data Folder path: " + Main.DATA_FOLDER + "/" + folderName);
        Log.d(TAG, "localCopyCache -> Cache Data Folder path: " + Main.CACHE_FOLDER + "/" + folderName);

        if (files != null) {
            for (File file : files) {
                new LocalFileCopy(getApplicationContext()).execute(file.getAbsolutePath(), Main.CACHE_FOLDER + "/" + folderName, "path");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Add photo to album*/
            case 12:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Photo added successfully",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "Photo adding aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*Add user to album*/
            case 13:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "User added successfully",
                            Toast.LENGTH_LONG).show();                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "User adding aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    @Override
    protected void loadData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();

        dialog.dismiss();
    }



    /**
     * Download catalogs or albuns
     * @param url
     */
    private List<String> downloadFile(String url, String description, String folderPath, String fileName, int option) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage(description);
        dialog.show();

        if (fileName == null) //get name from url
        {
            if (url.contains("?"))
                fileName = url.substring(url.lastIndexOf('/'), url.indexOf('?'));
            else fileName = url.substring(url.lastIndexOf('/'));
        }

        Log.i(TAG, "FileName: " + fileName);

        DownloadFileFromLink dft = new DownloadFileFromLink(ListPhoto.this, DropboxClientFactory.getClient(), new DownloadFileFromLink.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                dialog.dismiss();

            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Log.i(TAG, "Failed to download file.", e);
                Toast.makeText(ListPhoto.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        File result = null;
        try {
            result = dft.execute(url, folderPath, fileName).get();

            //DOWNLOAD CATALOGS

            if (option == 1) {
                FileReader fr = new FileReader(result);
                BufferedReader br = new BufferedReader(fr);

                String line, catalog = "";

                while ((line = br.readLine()) != null) {
                    catalog += line + "\n";
                }
                Log.i(TAG, "Catalog content: " + catalog);

                AlbumCatalog albumCatalog = AlbumCatalog.parseToAlbumCatalog(catalog);
                return albumCatalog.getPaths2Pics();
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}

