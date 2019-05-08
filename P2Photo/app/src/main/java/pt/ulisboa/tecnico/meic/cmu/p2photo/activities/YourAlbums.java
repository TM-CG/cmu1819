package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.FilesAdapter;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Files.EXTRA_PATH;


public class YourAlbums extends DropboxActivity implements Toolbar.OnMenuItemClickListener {
    private FilesAdapter mFilesAdapter;
    private String mPath;
    private Cache cacheInstance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_albums);




        String path = getIntent().getStringExtra(EXTRA_PATH);
        mPath = path == null ? "" : path;
        Log.d("caminho", mPath);


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
        cacheInstance = Cache.getInstance();
        cacheInstance.albumsList = (ListView) findViewById(R.id.lst_albums);
        cacheInstance.adapterTitle = new ArrayAdapter<String>(getApplicationContext(), R.layout.your_albums_list_layout, R.id.albumTitle, cacheInstance.ownedAlbumWithIDs);
        cacheInstance.albumsList.setAdapter(cacheInstance.adapterTitle);
        Log.d("YourAlbums", "Cache size: " + cacheInstance.ownedAlbumWithIDs.size());
        cacheInstance.albumsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cacheInstance.progressBar = (ProgressBar) findViewById(R.id.loading);
                cacheInstance.loadingSpinner(true);
                String selectedItem = parent.getItemAtPosition(position).toString();

                int albumId = Integer.parseInt(selectedItem.split(" ")[0]);
                String albumTitle = selectedItem.substring(selectedItem.indexOf(' '));
                startActivity(ListPhoto.getIntent(YourAlbums.this, albumId, albumTitle));
            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
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

    @Override
    protected void loadData() {
    }


}