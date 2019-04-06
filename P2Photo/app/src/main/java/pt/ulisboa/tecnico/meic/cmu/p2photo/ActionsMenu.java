package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity.sv;

public class ActionsMenu extends DropboxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_menu);
    }

    public void goBack(View view){
        new SignOut().execute();
    }

    public void selectSettings(View view) {
        Intent intent = new Intent(this, settingsActivity.class);
        startActivityForResult(intent, 4);
    }

    public void createAlbum(View view){
        Intent intent = new Intent(this, CreateAlbum.class);
        startActivityForResult(intent, 6);
    }

    public void showAlbums(View view){
        Intent intent = new Intent(this, YourAlbums.class);
        startActivityForResult(intent, 7);
    }

    public void addPhoto(View view){
        Intent intent = new Intent(this, addPhotoActivityFromMenu.class);
        startActivityForResult(intent, 8);
    }

    public void addUsersToAlbum(View view){
        Intent intent = new Intent(this, addUserFromMainMenu.class);
        startActivityForResult(intent, 9);
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
        }
    }

    public class SignOut extends AsyncTask {
        @Override
        protected String doInBackground(Object[] objects) {
            try {
                sv.logOut();
                return "OK";
            } catch (P2PhotoException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            String msg = (String) result;
            //Maybe a toast?
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void loadData() {

    }
}
