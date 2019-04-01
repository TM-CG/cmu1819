package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

public class chooseCloudLocalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cloud_local);
    }

    public void goBack(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }
}
