package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

public class P2PhotoAlert extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String options;

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_prompt);

        TextView lbl_warning = (TextView) findViewById(R.id.lbl_warning);
        lbl_warning.setText(getIntent().getExtras().getString("message"));

        options = getIntent().getExtras().getString("options");

        if (options != null && options.equals("onlyOK")) {
            Button cancel = findViewById(R.id.btn_cancel);
            cancel.setVisibility(View.INVISIBLE);
            Button confirm = findViewById(R.id.btn_confirm);
            confirm.setText("OK");
        }
    }

    /**
     * Callback method defined by the View
     * @param v
     */
    public void btnConfirm(View v) {
        setResult(RESULT_OK);
        P2PhotoAlert.this.finish();
    }

    /**
     * Callback method defined by the View
     * @param v
     */
    public void btnCancel(View v) {
        setResult(RESULT_CANCELED);
        P2PhotoAlert.this.finish();
    }
}
