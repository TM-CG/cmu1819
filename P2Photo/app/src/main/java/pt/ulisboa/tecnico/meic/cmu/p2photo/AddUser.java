package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class AddUser extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private ArrayList<String> items2;
    private ArrayAdapter<String> itemsAdapter2;
    private ListView lvItems2;
    private static final int CONFIRMATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

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

    public void confirmAddUsers(View view) {
        Intent intent = new Intent(AddUser.this, ConfirmPromptActivity.class);
        intent.putExtra("message", "Are you sure?");
        startActivityForResult(intent, CONFIRMATION_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIRMATION_REQUEST) {
            if (resultCode == RESULT_OK){
                //the user confirmed

            }
            else if (resultCode == RESULT_CANCELED) {
                //the user cancelled
            }
        }
    }
}
