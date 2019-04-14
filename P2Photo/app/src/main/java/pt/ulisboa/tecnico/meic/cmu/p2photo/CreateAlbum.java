package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.ListFolderResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class CreateAlbum extends AppCompatActivity {
    EditText album;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private ArrayList<String> items2;
    private ArrayAdapter<String> itemsAdapter2;
    private ListView lvItems2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
    }

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void create(View view){
        album = (EditText) findViewById(R.id.nameInput);

        items = new ArrayList<String>();

        lvItems = (ListView) findViewById(R.id.usersSearch);

        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);

        lvItems.setAdapter(itemsAdapter);

        items2 = new ArrayList<String>();

        lvItems2 = (ListView) findViewById(R.id.usersBeingAdded);

        itemsAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items2);

        lvItems2.setAdapter(itemsAdapter2);

        setupListViewListener();
        createUsersTest();

        //creates album on the server
        new CreateAlbumOnServer().execute();

        /*CloudStorage cs = new CloudStorage(CreateAlbum.this, 1, StorageProvider.Operation.READ);
        new Thread(cs, "ReadingThread").start();*/
    }
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        addUser(pos);

                        return true;
                    }

                });
        lvItems2.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        items2.remove(pos);
                        itemsAdapter2.notifyDataSetChanged();

                        return true;
                    }

                });
    }

    private void createUsersTest(){
        itemsAdapter.add("João");
        itemsAdapter.add("Carlos");
        itemsAdapter.add("Alberto");
        itemsAdapter.add("Gorila");
        itemsAdapter.add("Pulpo");
        itemsAdapter.add("Pardal");
        itemsAdapter.add("Vitor");
        itemsAdapter.add("Titas");
        itemsAdapter.add("Miguel");
        itemsAdapter.add("Samora");
    }

    private void addUser(int pos){
        // Remove the item within array at position
        if(!items2.contains(items.get(pos))) {
            items2.add(items.get(pos));
            // Refresh the adapter
            itemsAdapter2.notifyDataSetChanged();
            // Return true consumes the long click event (marks it handled)
        }
    }

    class CreateAlbumOnServer extends AsyncTask {

        private ServerConnector sv = MainActivity.sv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            Integer albumId = null;

            if (o != null) {
                albumId = (Integer) o;
            } else {
                Log.i("CreateAlbumOnServer", "albumId is null");
                return;
            }

            String albumTitle = album.getText().toString();

            //create album catalog for new album
            AlbumCatalog catalog = new AlbumCatalog(albumId, albumTitle);

            new Thread(new CloudStorage(CreateAlbum.this, catalog, StorageProvider.Operation.WRITE), "WritingThread").start();
            //create a new folder
            new CreateFolderTask().execute(albumId + " " + albumTitle,getApplicationContext());
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                return sv.createAlbum();
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}


class CreateFolderTask extends AsyncTask<Object,Object,Object[]> {

    @Override
    protected Object[] doInBackground(Object[] objects) {
        String albumName = (String) objects[0];
        Object[] result = new Object[2];
        result[0] = objects[1];
        try {
            CreateFolderResult res = DropboxClientFactory.getClient().files().createFolderV2("/" + albumName);
            result[1] = "OK";
            return result;
        } catch (DbxException e1) {
            result[1] = "NOK";
            return result;
        }
    }

    @Override
    protected void onPostExecute(Object[] result) {
        String res = (String) result[1];
        if(res == "OK"){
            Toast.makeText((Context)result[0], "Album created in your dropbox",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText((Context)result[0], "Album NOT created in your dropbox",
                    Toast.LENGTH_LONG).show();
        }
    }


}
