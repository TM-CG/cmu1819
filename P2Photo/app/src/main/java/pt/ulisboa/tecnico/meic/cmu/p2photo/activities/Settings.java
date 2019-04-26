package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

public class Settings extends AppCompatActivity {

    private SeekBar seekBarStorage;
    private SeekBar seekBarCache;
    private TextView textViewStorage;
    private TextView textViewCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        seekBarStorage = (SeekBar) findViewById(R.id.seekBarStorageSettings);
        textViewStorage = (TextView) findViewById(R.id.storageSettings);
        seekBarCache = (SeekBar) findViewById(R.id.seekBarCacheSettings);
        textViewCache = (TextView) findViewById(R.id.cacheSettings);

        // Initialize the textview with '0'.
        textViewStorage.setText(seekBarStorage.getProgress() + "/" + seekBarStorage.getMax());
        textViewCache.setText(seekBarCache.getProgress() + "/" + seekBarCache.getMax());


        seekBarStorage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewStorage.setText(progress + "/" + seekBar.getMax());
            }
        });

        seekBarCache.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewCache.setText(progress + "/" + seekBar.getMax());
            }
        });
    }

    public void goBack(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void saveSettings(View view){
        /*TODO save settings here*/
        Toast.makeText(getApplicationContext(), "Settings saved successfully",
                Toast.LENGTH_LONG).show();
    }

    public void showLog(View view){
        Intent intent = new Intent(this, Log.class);
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Show log option*/
            case 5:
                if(resultCode==RESULT_OK){

                }
                else if(resultCode==RESULT_CANCELED){

                }
                break;
        }
    }
}
