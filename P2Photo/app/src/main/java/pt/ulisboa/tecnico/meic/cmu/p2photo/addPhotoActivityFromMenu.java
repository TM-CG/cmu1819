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

import java.io.BufferedReader;
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

    private List<String> albums;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private static final int PICKFILE_REQUEST_CODE = 1;
    private Spinner sel_album;
    private AlbumCatalog catalog;

    private ArrayList<File> catalogFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_from_menu);

        sel_album = (Spinner) findViewById(R.id.sel_album);

        albums = new ArrayList<String>();
        catalogFiles = new ArrayList<>();

        spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, albums);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

        sel_album.setAdapter(spinnerArrayAdapter);

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

    private void uploadFile(String fileUri) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();

        String folderPath = sel_album.getSelectedItem().toString();
        Log.i(TAG, "Path to remote folder to upload: " + folderPath);

        new UploadFileTask(this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                dialog.dismiss();

                String message = "Successfully uploaded " + result.getName() + ": size " + result.getSize();
                Toast.makeText(addPhotoActivityFromMenu.this, message, Toast.LENGTH_SHORT)
                        .show();

                //File uploaded so lets add it to the catalog

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

    private void downloadFile(FileMetadata file) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Reading catalogs");
        dialog.show();

        new DownloadFileTask(this, DropboxClientFactory.getClient(), new DownloadFileTask.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                dialog.dismiss();
                if(result != null) {
                    try {

                        BufferedReader br = new BufferedReader(new FileReader(result));
                        String st;
                        st = br.readLine();
                        Log.d("readFile", st);
                        st = st.replace(System.getProperty("line.separator"), "");
                        if(! albums.contains(st)) {
                            albums.add(st);
                        }

                        spinnerArrayAdapter.notifyDataSetChanged();
                        catalogFiles.add(result);

                        Log.d("albumsTeste", albums.toString());

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

    @Override
    protected void loadData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait");
        dialog.show();

        new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                dialog.dismiss();
                if(result != null) {
                    try {
                        for(Metadata m : result.getEntries()){
                            if(m.getName().endsWith("_catalog.txt")) {
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
        }).execute("");
    }

    class UpdateAlbumCatalog extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String albumId = (String) objects[0];
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

            //append new photoURL


            return null;
        }
    }
}
