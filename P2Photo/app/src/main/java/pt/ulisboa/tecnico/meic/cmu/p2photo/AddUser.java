package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class AddUser extends AppCompatActivity {

    private static final int CONFIRMATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

    }

    public void confirmAddUsers(View view) {
        Intent intent = new Intent(AddUser.this, ConfirmPromptActivity.class);
        intent.putExtra("message", "Are you sure?");
        startActivityForResult(intent, CONFIRMATION_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIRMATION_REQUEST) {
            if (resultCode == RESULT_OK){
                //the user confirmed

            }
            else if (resultCode == RESULT_CANCELED) {
                //the user cancelled
            }
        }
    }
}
