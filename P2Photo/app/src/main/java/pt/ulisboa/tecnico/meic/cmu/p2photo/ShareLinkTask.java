package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.File;

/**
 * A task for creating shared links of a file
 */
public class ShareLinkTask extends AsyncTask<FileMetadata, Void, SharedLinkMetadata> {
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onUploadComplete(SharedLinkMetadata result);
        void onError(Exception e);
    }

    public ShareLinkTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(SharedLinkMetadata result) {
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
    protected SharedLinkMetadata doInBackground(FileMetadata... params) {
        FileMetadata metadata = params[0];

        Log.i("CloudStorage", "ShareLinkTask metadata: " + new Boolean(metadata == null).toString());

        if (metadata != null) {
            // Note - this is not ensuring the name is a valid dropbox file name
            Log.i("CloudStorage", "ShareLinkTask remoteFileName: " + metadata.getPathLower());

            try {
                return mDbxClient.sharing().createSharedLinkWithSettings(metadata.getPathLower());
            } catch (DbxException e) {
                mException = e;
                Log.i("CloudStorage", "ShareLinkTask doInBack: got exception!");
            }

        }

        return null;
    }
}
