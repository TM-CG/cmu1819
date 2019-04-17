package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.FilesActivity.EXTRA_PATH;


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
        cacheInstance.adapterTitle = new ArrayAdapter<String>(getApplicationContext(), R.layout.your_albums_list_layout, R.id.albumTitle, cacheInstance.ownedAndPartAlbums);
        cacheInstance.albumsList.setAdapter(cacheInstance.adapterTitle);
        cacheInstance.albumsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(ListPhoto.getIntent(YourAlbums.this, 1));
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

    @Override
    protected void loadData() {
    }


}