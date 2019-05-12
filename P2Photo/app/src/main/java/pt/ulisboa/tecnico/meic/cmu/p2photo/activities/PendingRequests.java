package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDFetchAllPendingCatalogs;

public class PendingRequests extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private String pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        items = new ArrayList<String>();

        lvItems = (ListView) findViewById(R.id.pendingRequests);

        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);

        lvItems.setAdapter(itemsAdapter);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cache.getInstance().progressBar = (ProgressBar) findViewById(R.id.loading);
                Cache.getInstance().loadingSpinner(true);
                Intent intent = new Intent(PendingRequests.this, ActionOnPending.class);
                pos = items.get(position);
                intent.putExtra("albumID", items.get(position));
                startActivityForResult(intent, 1);
            }
        });

        new pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.PendingRequests() {
            @Override
            protected void onPostExecute(Object[] result) {
                super.onPostExecute(result);
                //request and receive all catalogs from near by devices

                new WiFiDFetchAllPendingCatalogs().execute(result[1]);
            }
        }.execute(itemsAdapter);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /*Action performed on pending request*/
            case 1:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Action performed on pending request",
                            Toast.LENGTH_LONG).show();
                    items.remove(pos);
                    itemsAdapter.notifyDataSetChanged();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Action on pending request aborted",
                            Toast.LENGTH_LONG).show();

                }
                break;

        }
    }
}
