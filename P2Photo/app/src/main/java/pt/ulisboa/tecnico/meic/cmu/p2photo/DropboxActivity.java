package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.dropbox.core.android.Auth;

public abstract class DropboxActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
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
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        SharedPreferences prefs = getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        return accessToken != null;
    }
}


