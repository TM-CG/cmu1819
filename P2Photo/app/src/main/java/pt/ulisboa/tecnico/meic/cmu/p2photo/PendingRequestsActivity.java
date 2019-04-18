package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

public class PendingRequestsActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

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
                Intent intent = new Intent(PendingRequestsActivity.this, ActionOnPendingActivity.class);
                intent.putExtra("albumID", items.get(position));
                startActivityForResult(intent, 1);
            }
        });

        new PendingRequests().execute(itemsAdapter);


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
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Action on pending request aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}

class PendingRequests extends AsyncTask<Object,Void,Object[]> {
    @Override
    protected Object[] doInBackground(Object [] objects) {
        Object[] result = new Object[2];
        result[0] = objects[0];
        try {
            List<Integer> requests = MainActivity.getSv().listIncomingRequest();
            result[1] = requests;
            return result;
        } catch (P2PhotoException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object[] result) {
        if(result != null){
            ArrayAdapter<String> itemsAdapter = (ArrayAdapter<String>) result[0];
            Log.d("users", MainActivity.getUser());
            for(Integer id: (List<Integer>) result[1]){
                Log.d("ids", String.valueOf(id));
                itemsAdapter.add(String.valueOf(id));
            }
        }

    }
}
