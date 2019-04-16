package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.ListPhotoAdapter;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class ListPhoto extends DropboxActivity implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = ListPhoto.class.getName();

    private String catalogFile;
    private int albumId;

    private String mPath;
    private FilesAdapter mFilesAdapter;
    private FileMetadata mSelectedFile;

    public static Intent getIntent(Context context, int albumId) {
        Intent filesIntent = new Intent(context, ListPhoto.class);
        filesIntent.putExtra("album", albumId);
        return filesIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo);
        Log.i(TAG, "Setted content view!");
        this.albumId = getIntent().getExtras().getInt("album");
        Log.i(TAG, "Album: " + albumId);

        catalogFile = getIntent().getStringExtra("catalog");
        new FetchAllCatalogs().execute();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.addPhoto:
                intent = new Intent(this, addPhotoActivity.class);
                startActivityForResult(intent, 12);
                return true;
            case R.id.addUser:
                intent = new Intent(this, AddUser.class);
                startActivityForResult(intent, 13);
                return true;

        }
        return false;
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

    class FetchAllCatalogs extends AsyncTask {
        private ServerConnector sv = MainActivity.sv;
        @Override
        protected List<String> doInBackground(Object [] objects) {
            try {
                return sv.listUserAlbumSlices(albumId);
            } catch (P2PhotoException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            List<String> urls = (List<String>) result;
            //Already fetch all catalogs from server now lets download it
            Log.i(TAG, "Downloaded catalogs path from server " + urls.size());

            for (String url : urls) {
                downloadFile(url, "Loading catalogs", "", "tmp_catalog.txt", 1);
            }

        }

    }

    /**
     * Download catalogs or albuns
     * @param url
     * @param option
     */
    private void downloadFile(String url, String description, String folderPath, String fileName, final int option) {
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

        new DownloadFileFromLinkTask(ListPhoto.this, DropboxClientFactory.getClient(), new DownloadFileFromLinkTask.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                dialog.dismiss();

                try {
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
                        List<String> pURL = albumCatalog.getPaths2Pics();

                        //Downloading photos (invoke same function with different option to prevent
                        //useful infinite recursions

                        //Create folder with albumId and title


                        Log.i(TAG, "Catalog #urls: " + pURL.size());
                        String tmpFolderPath = albumCatalog.getAlbumId() + " "
                                + albumCatalog.getAlbumTitle();

                        //Create temporary folder
                        File folder = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS + "/" + tmpFolderPath);
                        folder.mkdir();

                        for (String photoURL : pURL) {
                            downloadFile(photoURL, "Loading photo", tmpFolderPath, null, 0);
                        }
                        Log.i(TAG, "Successfully added path of pictures to array!");
                    }


                    } catch(FileNotFoundException e){
                        Log.i(TAG, "FileNotFound!");
                    } catch(IOException e){
                        Log.i(TAG, "IOException!");
                    }


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
        }).execute(url, folderPath, fileName);

    }
}

