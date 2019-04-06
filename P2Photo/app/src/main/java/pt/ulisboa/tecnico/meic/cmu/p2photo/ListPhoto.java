package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.addPhoto:
                intent = new Intent(this, addPhotoActivity.class);
                startActivityForResult(intent, 12);
                return true;
            case R.id.addUser:
                intent = new Intent(this, AddUser.class);
                startActivityForResult(intent, 13);
                return true;

        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Add photo to album*/
            case 12:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Photo added successfully",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "Photo adding aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*Add user to album*/
            case 13:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "User added successfully",
                            Toast.LENGTH_LONG).show();                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "User adding aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
