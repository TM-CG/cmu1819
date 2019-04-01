package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
}
