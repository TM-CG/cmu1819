package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class signuplocalstorageactivity extends AppCompatActivity {

    private SeekBar seekBarStorage;
    private SeekBar seekBarCache;
    private TextView textViewStorage;
    private TextView textViewCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuplocalstorageactivity);
        seekBarStorage = (SeekBar) findViewById(R.id.seekBarStorage);
        textViewStorage = (TextView) findViewById(R.id.storage);
        seekBarCache = (SeekBar) findViewById(R.id.seekBarCache);
        textViewCache = (TextView) findViewById(R.id.cache);

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
}
