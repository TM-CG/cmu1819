package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;


public class chooseCloudLocalActivity extends DropboxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cloud_local);
    }

    public void goBack(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void selectAuthCloud(View view){
        Auth.startOAuth2Authentication(chooseCloudLocalActivity.this, getString(R.string.app_key));

        Button cloudButton = (Button) findViewById(R.id.cloudButton);
        cloudButton.setEnabled(true);
    }

    public void selectCloud(View view){
        Intent intent = new Intent(this, ActionsMenu.class);
        startActivity(intent);
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                Log.i("DROPBOX", result.getEmail());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Choose cloud option*/
            case 3:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;
        }
    }
}
