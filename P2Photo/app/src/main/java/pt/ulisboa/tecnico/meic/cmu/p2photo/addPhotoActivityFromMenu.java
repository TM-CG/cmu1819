package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class addPhotoActivityFromMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_from_menu);

        Spinner sel_album = (Spinner) findViewById(R.id.sel_album);
        String[] albums = {"Album de ferias", "Album de LEIC", "Churrasco", "Gorilada Distribuida <3", "Almoços do Social", "Discussão de projetos", "Natal",
                "Páscoa", "Praxe"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, albums);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sel_album.setAdapter(spinnerArrayAdapter);
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
        startActivityForResult(intent, 14);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /*Choose select photo option*/
            case 14:
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
