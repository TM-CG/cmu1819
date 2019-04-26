package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

public class ConfirmPrompt extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_prompt);

        TextView lbl_warning = (TextView) findViewById(R.id.lbl_warning);
        lbl_warning.setText(getIntent().getExtras().getString("message"));
    }

    /**
     * Callback method defined by the View
     * @param v
     */
    public void btnConfirm(View v) {
        setResult(RESULT_OK);
        ConfirmPrompt.this.finish();
    }

    /**
     * Callback method defined by the View
     * @param v
     */
    public void btnCancel(View v) {
        setResult(RESULT_CANCELED);
        ConfirmPrompt.this.finish();
    }
}
