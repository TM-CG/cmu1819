package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.text.DateFormat;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.ShareLink;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.UploadFile;

/**
 * Support for storing catalog files in the cloud
 */
public class CloudStorage extends StorageProvider {

    /** Prefix of catalog files **/
    public static final String CATALOG_SUFFIX = "%d_catalog.txt";

    private static final String TAG = CloudStorage.class.getName();

    public CloudStorage(Context context, AlbumCatalog catalog, Operation operation) {
        super(context, catalog, operation);
    }

    public CloudStorage(Context context, int albumId, Operation operation) {
        super(context, albumId, operation);
    }

    @Override
    void writeFile(final String fileURL) {

        final int albumId = getCatalog().getAlbumId();
        //Writing file to a cloud provider is equivalent to upload it

        //vitor: i just remove the dialog because CreateAlbum closes so fast that dialog is running
        //only after this activity closes! :)

        /*final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Writing catalog file");
        dialog.show();*/


        FileMetadata result = null;
        try {
            result = new UploadFile(getContext(), DropboxClientFactory.getClient(), new UploadFile.Callback() {

                @Override
                public void onUploadComplete(FileMetadata result) {
                    String message = result.getName() + " size " + result.getSize() + " modified " +
                            DateFormat.getDateTimeInstance().format(result.getClientModified());
                    /*Toast.makeText(getContext(), message, Toast.LENGTH_SHORT)
                            .show();*/
                    Log.i(TAG, message);
                    //After upload let's share the catalog in order to every with the link be able
                    //to read it

                }


                @Override
                public void onError(Exception e) {
                    //dialog.dismiss();

                    Log.i(TAG, "Failed to upload file.", e);
                    /*Toast.makeText(getContext(),
                            "An error has occurred",
                            Toast.LENGTH_SHORT)
                            .show();*/

                }

                //wait until the execution finishes
            }).execute(fileURL, "").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        SharedLinkMetadata linkMetadata = null;
        try {
            linkMetadata = new ShareLink(getContext(), DropboxClientFactory.getClient(), new ShareLink.Callback() {
                @Override
                public void onShareComplete(SharedLinkMetadata result) {
                    Log.i(TAG, "Successfully generated link to shared file: " + result.getUrl());

                    //Set that url to the server

                }

                @Override
                public void onError(Exception e) {
                    Log.i(TAG, "There was an error in generating the shared link!");
                }
                //need to wait this thread until asynctask finishes
            }).execute(result).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        try {
            new AddAlbumSliceCatalogURL().execute(albumId, linkMetadata.getUrl()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    String readFile(String fileURL) {
        return null;
    }

    @Override
    public void loadData() {

    }
}

