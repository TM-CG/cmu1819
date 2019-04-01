package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.ListPhotoAdapter;

public class selectPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        //Set GridView for thumbnails preview using the ListPhotoAdapter
        GridView gridView = (GridView) findViewById(R.id.grid_thumbnails);
        ListPhotoAdapter photoAdapter = new ListPhotoAdapter(this);
        gridView.setAdapter(photoAdapter);
    }
}
