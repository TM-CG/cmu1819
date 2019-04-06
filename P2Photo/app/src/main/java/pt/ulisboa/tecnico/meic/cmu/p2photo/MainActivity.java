package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private EditText user;
    private EditText pass;
    public static ServerConnector sv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*try connection to server*/
        new SocketConnect().execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            sv.terminateConn();
        } catch (P2PhotoException e) {
            Log.d("serverTest", "Connection not closed!");
        }
    }


    public void signIn(View view) {
        intent = new Intent(this, ActionsMenu.class);
        if(checkArguments()){
            new SignIn().execute();
        }
    }

    public void signUp(View view) {
        intent = new Intent(this, chooseCloudLocalActivity.class);
        if(checkArguments()){
            new SignUp().execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            /*Sign In*/
            case 1:
                /*Sign Up*/
            case 2:
                if(resultCode==RESULT_OK){
                    Toast.makeText(getApplicationContext(), "User "
                                    + data.getStringExtra("name") + " logged out",
                            Toast.LENGTH_LONG).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "User " + user.getText().toString() + " logged out abruptly",
                            Toast.LENGTH_LONG).show();
                }
                user.setText("");
                pass.setText("");
                break;
        }
    }

    public boolean checkArguments(){
        user = (EditText) findViewById(R.id.textUser);
        pass = (EditText) findViewById(R.id.textPass);
        intent.putExtra("name", user.getText().toString());
        intent.putExtra("pass", pass.getText().toString());

        setResult(RESULT_OK, intent);

        if((user.getText().toString().matches("")) || pass.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Name or password invalid",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public class SocketConnect extends AsyncTask {
        @Override
        protected ServerConnector doInBackground(Object [] objects) {
            try {
                ServerConnector tmp = new ServerConnector("192.168.1.66", 10001);
                return tmp;
            } catch (P2PhotoException e) {
                Log.d("serverTest", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if(result != null) {
                sv = (ServerConnector) result;
                Log.d("serverTest", "Connection established");
            }
        }
    }

    public class SignUp extends AsyncTask {
        @Override
        protected String doInBackground(Object [] objects) {
            try {
                sv.signUp(user.getText().toString(), pass.getText().toString());
                new SignIn().execute();
                return "OK";
            } catch (P2PhotoException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            String msg = (String) result;
            doToast(msg);

        }

    }

    public class SignIn extends AsyncTask {
        @Override
        protected String doInBackground(Object [] objects) {
            try {
                sv.logIn(user.getText().toString(), pass.getText().toString());
                startActivityForResult(intent, 2);
                return "OK";
            } catch (P2PhotoException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            String msg = (String) result;
            doToast(msg);

        }

    }

    public void doToast(String data){
        switch (data){
            case "OK":
                break;
            case "NOK 1":
                Toast.makeText(getApplicationContext(), "User name not found",
                        Toast.LENGTH_LONG).show();
                break;
            case "NOK 2":
                Toast.makeText(getApplicationContext(), "User name and password don't match",
                        Toast.LENGTH_LONG).show();
                break;
            case "NOK 3":
                Toast.makeText(getApplicationContext(), "User name already in use",
                        Toast.LENGTH_LONG).show();
                break;

        }
    }
}
