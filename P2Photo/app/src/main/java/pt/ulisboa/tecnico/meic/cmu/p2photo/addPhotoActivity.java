package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class addPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
    }

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void create(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void selectPhoto(View view) {
        Intent intent = new Intent(this, selectPhotoActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /*Choose select photo option*/
            case 10:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Photo selected successfully",
                            Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Photo selection aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
