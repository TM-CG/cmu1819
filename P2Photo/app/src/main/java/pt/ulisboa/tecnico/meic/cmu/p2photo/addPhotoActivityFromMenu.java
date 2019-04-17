package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage.CATALOG_SUFFIX;

public class addPhotoActivityFromMenu extends DropboxActivity {
    private static final String TAG = addPhotoActivityFromMenu.class.getName();

    private static final int PICKFILE_REQUEST_CODE = 1;


    private AlbumCatalog catalog;
    private Cache cacheInstance;
    private ArrayList<File> catalogFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_from_menu);
        cacheInstance = Cache.getInstance();

        cacheInstance.sel_album = (Spinner) findViewById(R.id.sel_album);
        catalogFiles = new ArrayList<>();

        cacheInstance.spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, cacheInstance.ownedAlbums);
        cacheInstance.spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

        cacheInstance.sel_album.setAdapter(cacheInstance.spinnerArrayAdapter);

    }

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void add(View view){

        launchFilePicker();
        /*Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();*/
    }

    public void selectPhoto(View view) {
        Intent intent = new Intent(this, selectPhotoActivity.class);
        startActivityForResult(intent, 14);
    }

    private void launchFilePicker() {
        // Launch intent to pick file for upload
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    private void uploadFile(final String fileUri) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();

        final String folderPath = cacheInstance.sel_album.getSelectedItem().toString();
        final int albumId = Integer.parseInt(folderPath.split(" ")[0]);

        Log.i(TAG, "Path to remote folder to upload: " + folderPath);

        new UploadFileTask(this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                dialog.dismiss();

                String message = "Successfully uploaded " + result.getName() + ": size " + result.getSize();
                Toast.makeText(addPhotoActivityFromMenu.this, message, Toast.LENGTH_SHORT)
                        .show();

                Log.i(TAG, "UploadFileTask: Album: " + albumId);

                //Generate link for that photo
                new ShareLinkTask(addPhotoActivityFromMenu.this, DropboxClientFactory.getClient(), new ShareLinkTask.Callback() {
                    @Override
                    public void onShareComplete(SharedLinkMetadata result) {
                        //File uploaded so lets add it to the catalog
                        Log.i(TAG, "UploadFileTask: URL 4 pic: " + result.getUrl());
                        Log.i(TAG, "Successfully generated link for new picture");

                        //vitor: bah :)
                        new UpdateAlbumCatalog() {
                            @Override
                            protected void onPostExecute(Object o) {
                                Log.i(TAG, "Successfully created local album catalog");

                                String filePath;
                                if (o != null) {
                                    filePath = (String) o;

                                    //After updating the local Album Catalog it is necessary to upload it again
                                    new UploadFileTask(addPhotoActivityFromMenu.this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
                                        @Override
                                        public void onUploadComplete(FileMetadata result) {
                                            Log.i(TAG, "Uploading catalog after update: success");
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.i(TAG, "Uploading catalog after update: error");
                                        }
                                    }).execute(filePath, "");
                                }
                            }
                        }.execute(albumId, result.getUrl());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.i(TAG, "Error on creating share link on uploading new photo!");
                    }
                }).execute(result);


            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Log.i(TAG, "Failed to upload file.", e);
                Toast.makeText(addPhotoActivityFromMenu.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(fileUri, folderPath, "useContext");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // This is the result of a call to launchFilePicker
                uploadFile(data.getData().toString());
            }
        }
    }

    @Override
    protected void loadData() {
    }

    class UpdateAlbumCatalog extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Integer albumId = (Integer) objects[0];
            String photoURL = (String) objects[1];
            String fileName = String.format(CATALOG_SUFFIX, albumId);
            File catalogFile = null;

            //search for catalog
            for (File f : catalogFiles) {
                if (f.getName().equals(fileName)) {
                    catalogFile = f;
                    break;
                }
            }

            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;

            if (catalogFile != null) {
                //append new photoURL
                try {
                    fileWriter = new FileWriter(catalogFile.getAbsoluteFile(), true);
                    bufferedWriter = new BufferedWriter(fileWriter);
                    Log.i(TAG, "UpdateAlbumCatalog: Writing URL to catalog: " + photoURL);
                    bufferedWriter.write(photoURL + "\n");

                } catch (IOException e) {
                    Log.i(TAG, "UpdateAlbumCatalog: IOException");
                } finally {

                    try {

                        if (bufferedWriter != null)
                            bufferedWriter.close();

                        if (fileWriter != null)
                            fileWriter.close();

                    } catch (IOException ex) {

                        Log.i(TAG, "UpdateAlbumCatalog: IOException when closing");

                    }
                }
            }
            else {
                Log.i(TAG, "UpdateAlbumCatalog: catalogFile is null!");
            }

            if (catalogFile == null)
                return null;
            return catalogFile.getAbsolutePath();
        }
    }
}
