package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

public class clientLog extends AppCompatActivity {
    private Cache cacheInstance;
    private ListView lvOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_log);

        cacheInstance = Cache.getInstance();

        cacheInstance.clientLogAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, cacheInstance.clientLog);

        lvOperations = (ListView) findViewById(R.id.lv);
        lvOperations.setAdapter(cacheInstance.clientLogAdapter);

    }
}
