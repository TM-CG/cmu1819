package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.core.android.Auth;

import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.PicassoClient;

public abstract class DropboxActivity extends AppCompatActivity {
    private static String accessToken;
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        accessToken = prefs.getString("access-token", null);
        Log.i("MR TOKEN", "pls mr. token: " + new Boolean(accessToken == null));
        if(accessToken == null){
            accessToken = Auth.getOAuth2Token();
            if(accessToken != null){
                prefs.edit().putString("access-token", accessToken).apply();
                initAndLoadData(accessToken);
            }
        }
        else {
            initAndLoadData(accessToken);
        }

        String uid = Auth.getUid();
        String storeUid = prefs.getString("user-id", null);
        if(uid != null && !uid.equals(storeUid)) {
            prefs.edit().putString("user-id", uid).apply();
        }
    }

    public void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        PicassoClient.init(getApplicationContext(), DropboxClientFactory.getClient());
        loadData();
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        SharedPreferences prefs = getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        return accessToken != null;
    }

    public static String getToken(){
        return accessToken;
    }
}


