package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
}
