package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ActionOnPendingActivity extends AppCompatActivity {
    private TextView albumIDtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_on_pending);
        albumIDtv = (TextView)findViewById(R.id.albumIDtext);
        Intent intent = getIntent();
        albumIDtv.setText(intent.getStringExtra("albumID"));
    }
}
