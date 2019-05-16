package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AddUsersToAlbum;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.FindUsers;

public class AddUserFromMainMenu extends P2PhotoActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private ArrayList<String> items2;
    private ArrayAdapter<String> itemsAdapter2;
    private ListView lvItems2;

    private Cache cacheInstance;


    private static final int CONFIRMATION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_from_main_menu);

        cacheInstance = Cache.getInstance();
        createStrucures();


        setupListViewListener();
        new FindUsers().execute(itemsAdapter);

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

    private void createStrucures(){
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

        cacheInstance.lvItemsSpinner = (Spinner) findViewById(R.id.spinner1);
        cacheInstance.itemsAdapterSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, cacheInstance.ownedAlbums);
        cacheInstance.itemsAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cacheInstance.lvItemsSpinner.setAdapter(cacheInstance.itemsAdapterSpinner);

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

    /*public void confirmAddUsers(View view) {
        Intent intent = new Intent(AddUserFromMainMenu.this, P2PhotoAlert.class);
        intent.putExtra("message", "Are you sure?");
        startActivityForResult(intent, CONFIRMATION_REQUEST);
    }*/

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void create(View view){
        Cache.getInstance().progressBar = (ProgressBar) findViewById(R.id.loading);
        Cache.getInstance().loadingSpinner(true);
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        String name = cacheInstance.lvItemsSpinner.getSelectedItem().toString();
        Object[] o = new Object[2];
        //Log.d("addUsers", items2.toString());
        o[0] = cacheInstance.albumsIDs.get(cacheInstance.albums.indexOf(name));
        //Log.d("addUsers", String.valueOf(o[0]));
        o[1] = items2;
        new AddUsersToAlbum().execute(o);
        finish();
    }

}
