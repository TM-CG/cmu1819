package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;

import java.text.DateFormat;

import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.UploadFileTask;

/**
 * Support for storing catalog files in the cloud
 */
public class CloudStorage extends StorageProvider {

    private static final String TAG = CloudStorage.class.getName();

    public CloudStorage(Context context, AlbumCatalog catalog, Operation operation) {
        super(context, catalog, operation);
    }

    public CloudStorage(Context context, int albumId, Operation operation) {
        super(context, albumId, operation);
    }

    @Override
    void writeFile(String fileURL) {

        //Writing file to a cloud provider is equivalent to upload it

        //vitor: i just remove the dialog because CreateAlbum closes so fast that dialog is running
        //only after this activity closes! :)

        /*final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Writing catalog file");
        dialog.show();*/

        new UploadFileTask(getContext(), DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                //dialog.dismiss();

                String message = result.getName() + " size " + result.getSize() + " modified " +
                        DateFormat.getDateTimeInstance().format(result.getClientModified());
                /*Toast.makeText(getContext(), message, Toast.LENGTH_SHORT)
                        .show();*/
                Log.i(TAG, message);
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
        }).execute(fileURL, "");
    }

    @Override
    String readFile(String fileURL) {
        return null;
    }

    @Override
    public void loadData() {

    }
}
