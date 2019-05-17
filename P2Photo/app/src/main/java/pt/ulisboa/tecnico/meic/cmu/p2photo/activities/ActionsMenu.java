package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import java.sql.Timestamp;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AntiMironeInitKeys;

public class ActionsMenu extends P2PhotoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_menu);

        if (Main.STORAGE_TYPE == Main.StorageType.CLOUD) {
            new AntiMironeInitKeys().execute();
        }
        //
    }

    public void goBack(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void selectSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivityForResult(intent, 4);
    }

    public void createAlbum(View view){
        Intent intent = new Intent(this, CreateAlbum.class);
        startActivityForResult(intent, 6);
    }

    public void showAlbums(View view){
        Cache.getInstance().clientLog.add(Main.username + " started watching albuns list at " + new Timestamp(System.currentTimeMillis()));
        Intent intent = new Intent(this, YourAlbums.class);
        startActivityForResult(intent, 7);
    }

    public void addPhoto(View view){
        Intent intent = new Intent(this, AddPhotoFromMainMenu.class);
        startActivityForResult(intent, 8);
    }

    public void addUsersToAlbum(View view){
        Intent intent = new Intent(this, AddUserFromMainMenu.class);
        startActivityForResult(intent, 9);
    }

    public void watchPending(View view){
        Intent intent = new Intent(this, PendingRequests.class);
        startActivityForResult(intent, 10);
    }
    public void checkClientLog(View view) {
        //Log.i("ClientLog", ""+Cache.getInstance().clientLog.toString());
        Intent intent = new Intent(this, clientLog.class);
        startActivityForResult(intent, 11);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Choose setting option*/
            case 4:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;
            /*Choose create album option*/
            case 6:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Album created successfully",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "Album creation aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*Choose your albums option*/
            case 7:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;
            /*Choose add photo option*/
            case 8:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Photo added successfully",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "Photo adding aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*Choose add users to album option*/
            case 9:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "User added successfully",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "User adding aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*pendings list*/
            case 10:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;
        }
    }

    @Override
    protected void loadData() {

    }
}
