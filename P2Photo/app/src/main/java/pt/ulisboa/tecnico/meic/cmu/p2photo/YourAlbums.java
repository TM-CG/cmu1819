package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class YourAlbums extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_albums);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.albums_menu);


        myToolbar.setOnMenuItemClickListener(this);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back button click here
                Intent intent = getIntent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        ListView albumsList = (ListView) findViewById(R.id.lst_albums);
        //DEBUG ONLY! TO BE REMOVED
        final String[] albums = {"Album de ferias", "Album de LEIC", "Churrasco", "Gorilada Distribuida <3", "Almoços do Social", "Discussão de projetos", "Natal",
        "Páscoa", "Praxe"};
        ArrayAdapter<String> adapterTitle = new ArrayAdapter<String>(this, R.layout.your_albums_list_layout, R.id.albumTitle, albums);
        albumsList.setAdapter(adapterTitle);

        albumsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(YourAlbums.this, ListPhoto.class);
                startActivityForResult(intent, 11);
            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_album:
                Intent intent = new Intent(this, CreateAlbum.class);
                startActivityForResult(intent, 10);
                return true;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Create album inside show albums activity*/
            case 10:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Album created successfully",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "Album creation aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*View album content*/
            case 11:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;

        }
    }
}
