package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AddUsersToAlbum;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.CreateAlbumOnServer;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.CreateFolder;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.FindUsers;

public class CreateAlbum extends P2PhotoActivity {
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

        new FindUsers().execute(itemsAdapter);


    }

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void create(View view){
        EditText albumTitle = (EditText) findViewById(R.id.nameInput);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);

        if ((!albumTitle.getText().toString().matches("\\s+")) && (!albumTitle.getText().toString().equals(""))) {

            //creates album on the server
            CreateAlbumOnServer createAlbumOnServer = new CreateAlbumOnServer() {
                @Override
                protected void onPostExecute(Object[] o) {
                    super.onPostExecute(o);
                    int albumId = (int) o[0];
                    new AddUsersToAlbum().execute(albumId, items2);
                    //Hides the progressBar
                    progressBar.setVisibility(View.INVISIBLE);
                }
            };

            createAlbumOnServer.execute(album.getText().toString(), getApplicationContext(), items2);
        }


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

    private void addUser(int pos){
        // Remove the item within array at position
        if(!items2.contains(items.get(pos))) {
            items2.add(items.get(pos));
            // Refresh the adapter
            itemsAdapter2.notifyDataSetChanged();
            // Return true consumes the long click event (marks it handled)
        }
    }

}


