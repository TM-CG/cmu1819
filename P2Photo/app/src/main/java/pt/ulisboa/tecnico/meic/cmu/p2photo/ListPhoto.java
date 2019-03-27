package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.ListPhotoAdapter;

public class ListPhoto extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.list_photo_menu);


        myToolbar.setOnMenuItemClickListener(this);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back button click here
                Toast.makeText(ListPhoto.this, "Back Button", Toast.LENGTH_SHORT).show();
            }
        });

        //Set GridView for thumbnails preview using the ListPhotoAdapter
        GridView gridView = (GridView) findViewById(R.id.grid_thumbnails);
        ListPhotoAdapter photoAdapter = new ListPhotoAdapter(this);
        gridView.setAdapter(photoAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.addPhoto:
                Toast.makeText(this, "You selected Add Photo", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.addUser:
                Toast.makeText(this, "You selected Add User", Toast.LENGTH_SHORT).show();
                return true;

        }
        return false;
    }
}
