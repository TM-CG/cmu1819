package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.io.File;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.DownloadFile;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.ListFolder;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.ShareLink;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.UploadFile;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.UpdateAlbumCatalog;

public class AddPhotoFromMainMenu extends DropboxActivity {
    private static final String TAG = AddPhotoFromMainMenu.class.getName();

    private static final int PICKFILE_REQUEST_CODE = 1;


    private AlbumCatalog catalog;
    private Cache cacheInstance;
    private File catalogFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_from_menu);
        cacheInstance = Cache.getInstance();

        cacheInstance.sel_album = (Spinner) findViewById(R.id.sel_album);

        //create a cache string description in the following format: albumID albumTitle
        cacheInstance.spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, cacheInstance.ownedAlbumWithIDs);
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

        selectCatalogFile(cacheInstance.sel_album.getSelectedItem().toString());
        /*Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();*/
    }

    public void selectPhoto(View view) {
        Intent intent = new Intent(this, SelectPhoto.class);
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

        new UploadFile(this, DropboxClientFactory.getClient(), new UploadFile.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                dialog.dismiss();

                String message = "Successfully uploaded " + result.getName() + ": size " + result.getSize();
                Toast.makeText(AddPhotoFromMainMenu.this, message, Toast.LENGTH_SHORT)
                        .show();

                Log.i(TAG, "UploadFile: Album: " + albumId);

                //Generate link for that photo
                new ShareLink(AddPhotoFromMainMenu.this, DropboxClientFactory.getClient(), new ShareLink.Callback() {
                    @Override
                    public void onShareComplete(SharedLinkMetadata result) {
                        //File uploaded so lets add it to the catalog
                        Log.i(TAG, "UploadFile: URL 4 pic: " + result.getUrl());
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
                                    new UploadFile(AddPhotoFromMainMenu.this, DropboxClientFactory.getClient(), new UploadFile.Callback() {
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
                        }.execute(catalogFile, albumId, result.getUrl());
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
                Toast.makeText(AddPhotoFromMainMenu.this,
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

    /**
     * Selects the file that corresponds to the catalog that the user choose in order to
     * download it (temporarily)
     * @param selectedOption the string in the format: <albumID> <albumTitle>
     */
    private void selectCatalogFile(String selectedOption) {

        final int albumId = Integer.parseInt(selectedOption.split(" ")[0]);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait");
        dialog.show();

        new ListFolder(DropboxClientFactory.getClient(), new ListFolder.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                dialog.dismiss();
                if(result != null) {
                    try {
                        for(Metadata m : result.getEntries()){
                            if(m.getName().equals(albumId + "_catalog.txt")) {
                                Log.i(TAG, "selectCatalogFile: Downloading catalog " + m.getName());
                                downloadFile((FileMetadata) m);
                                break;
                            }
                        }

                    } catch (Exception e) {
                        Log.i(TAG, "selectCatalogFile: IOException when downloading ");
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

                    catalogFile = result;
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
