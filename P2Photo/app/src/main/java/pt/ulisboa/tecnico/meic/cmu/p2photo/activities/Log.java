package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

public class Log extends P2PhotoActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
    }

    public void goBack(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }
}
