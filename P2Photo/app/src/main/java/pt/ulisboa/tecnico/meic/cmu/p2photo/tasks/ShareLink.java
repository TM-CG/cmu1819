package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.util.List;

/**
 * A task for creating shared links of a file
 */
public class ShareLink extends AsyncTask<FileMetadata, Void, SharedLinkMetadata> {
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onShareComplete(SharedLinkMetadata result);
        void onError(Exception e);
    }

    public ShareLink(Context context, DbxClientV2 dbxClient, Callback callback) {
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
            mCallback.onShareComplete(result);
        }
    }

    @Override
    protected SharedLinkMetadata doInBackground(FileMetadata... params) {
        FileMetadata metadata = params[0];

        Log.i("CloudStorage", "ShareLink metadata: " + new Boolean(metadata == null).toString());

        if (metadata != null) {
            // Note - this is not ensuring the name is a valid dropbox file name
            Log.i("CloudStorage", "ShareLink remoteFileName: " + metadata.getPathLower());

            try {

                ListSharedLinksResult listSharedLinksResult = mDbxClient.sharing()
                        .listSharedLinksBuilder()
                        .withPath(metadata.getPathLower()).withDirectOnly(true)
                        .start();

                List<SharedLinkMetadata> links = listSharedLinksResult.getLinks();

                if ((links != null) && (links.size() > 0)) {
                    SharedLinkMetadata sharedLinkMetadata = links.get(0);
                    Log.i("CloudStorage", "Existing url found: " + sharedLinkMetadata.getUrl());
                    return sharedLinkMetadata;
                }


                //create new shared link
                return mDbxClient.sharing().createSharedLinkWithSettings(metadata.getPathLower());


            } catch (DbxException e) {
                mException = e;
                Log.i("CloudStorage", "ShareLink doInBack: got exception!" + e.getMessage());
            }

        }

        return null;
    }
}
