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
    private ArrayList<String> albums;
    private ArrayAdapter<String> adapterTitle;
    private ListView albumsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_albums);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        mPath = path == null ? "" : path;

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
        albumsList = (ListView) findViewById(R.id.lst_albums);
        albums = new ArrayList<String>();
        adapterTitle = new ArrayAdapter<String>(getApplicationContext(), R.layout.your_albums_list_layout, R.id.albumTitle, albums);
        albumsList.setAdapter(adapterTitle);

        albumsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(FilesActivity.getIntent(YourAlbums.this, ""));
            }
        });

        loadData();



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
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();

        new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                dialog.dismiss();
                if(result != null) {
                    try {
                        Log.d("entradas", result.getEntries().toString());
                        for(Metadata m : result.getEntries()){
                            Log.d("entrei", "estou nas metadatas");
                            if(m.getName().endsWith("_catalog.txt")) {
                                Log.d("entrei", "sou um catalog");
                                downloadFile((FileMetadata) m);
                            }
                        }

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();


            }
        }).execute(mPath);
    }

    private void downloadFile(FileMetadata file) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Reading catalogs");
        dialog.show();

        new DownloadFileTask(this, DropboxClientFactory.getClient(), new DownloadFileTask.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                dialog.dismiss();
                if(result != null) {
                    try {

                        BufferedReader br = new BufferedReader(new FileReader(result));
                        String st;
                        while ((st = br.readLine()) != null) {
                            Log.d("readFile", st);
                            st = st.replace(System.getProperty("line.separator"), "");
                            if(! albums.contains(st)) {
                                albums.add(st);
                            }
                            break;
                        }
                        adapterTitle.notifyDataSetChanged();
                        Log.d("albumsTeste", albums.toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Log.i("download", "fail");

            }
        }).execute(file);

    }
}