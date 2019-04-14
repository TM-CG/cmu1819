package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Async task to upload a file to a directory
 */
public class UploadFileTask extends AsyncTask<String, Void, FileMetadata> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onUploadComplete(FileMetadata result);
        void onError(Exception e);
    }

    public UploadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(FileMetadata result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    protected FileMetadata doInBackground(String... params) {
        String localUri = params[0];
        File localFile = null;

        //Support for file path or context://URL
        if (params.length > 2) {
            if (params[2] != null)
                localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri));
        }
        else
            localFile = new File(localUri);

        Log.i("CloudStorage", "LocalURI: " + localUri);
        Log.i("CloudStorage", "localFile: " + new Boolean(localFile == null).toString());

        if (localFile != null) {
            String remoteFolderPath = params[1];

            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = localFile.getName();
            Log.i("CloudStorage", "UploadFileTask remoteFolderPath: " + remoteFolderPath);
            Log.i("CloudStorage", "UploadFileTask remoteFileName: " + remoteFileName);
            try (InputStream inputStream = new FileInputStream(localFile)) {
                String path;

                //support for uploading files in sub folders
                if (remoteFolderPath == "")
                    path = "/" + remoteFileName;
                else path = "/" + remoteFolderPath + "/" + remoteFileName;

                return mDbxClient.files().uploadBuilder(path)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            } catch (DbxException | IOException e) {
                mException = e;
                Log.i("CloudStorage", "UploadFileTask doInBack: got exception!");
            }
        }

        return null;
    }
}
