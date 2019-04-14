package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private static EditText user;
    private EditText pass;
    private EditText ip;
    private EditText port;
    public static ServerConnector sv;

    public static String getUser() {
        return user.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        if(checkConnectionParameters()){
            intent = new Intent(this, ActionsMenu.class);
            new SocketConnect().execute("signIn");
        }
    }

    public void signUp(View view) {
        //vitor: just for testing read and write of temp files
        /*AlbumCatalog catalog = new AlbumCatalog(1, "Album do zé");

        new Thread(new CloudStorage(this, catalog, StorageProvider.Operation.WRITE), "WritingThread").start();

        CloudStorage cs = new CloudStorage(this, null, StorageProvider.Operation.READ);
        new Thread(cs, "ReadingThread").start();*/

        if(checkConnectionParameters()){
            intent = new Intent(this, chooseCloudLocalActivity.class);
            new SocketConnect().execute("signUp");
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
                new LogOut().execute(resultCode,data);
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

    public boolean checkConnectionParameters(){
        /*try connection to server*/
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        if(ip.getText().toString().matches("") || port.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Ip and Port cannot be empty",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            try {
                Integer.parseInt(port.getText().toString());
                return true;
            } catch(NumberFormatException e){
                Toast.makeText(getApplicationContext(), "Port must be an integer",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }
    public class SocketConnect extends AsyncTask<Object,Void,Object[]> {
        @Override
        protected Object[] doInBackground(Object [] objects) {
            Object[] result = new Object[2];
            result[0] = objects[0];
            try {
                ServerConnector tmp = new ServerConnector(ip.getText().toString(), Integer.parseInt(port.getText().toString()));
                result[1] =  tmp;
                return result;
            } catch (P2PhotoException e) {
                result[1] =  null;
                return result;
            }
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if(result[1] == null) {
                sv = null;
                Toast.makeText(getApplicationContext(), "Connection not established",
                        Toast.LENGTH_LONG).show();
            }
            else{
                sv = (ServerConnector) result[1];
                if(result[0] == "signIn"){
                    if (checkArguments()) {
                        new SignIn().execute();
                    }
                }
                else if(result[0] == "signUp"){
                    if (checkArguments()) {
                        new SignUp().execute();
                    }
                }
            }
        }
    }

    public class LogOut extends AsyncTask<Object,Void,Object[]> {
        @Override
        protected Object[] doInBackground(Object [] objects) {
            try {
                sv.logOut();
            } catch (P2PhotoException e) {
                Toast.makeText(getApplicationContext(), "Server side problem logging out",
                        Toast.LENGTH_LONG).show();
            }
            return objects;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            Integer res = (Integer) result[0];
            if(result != null) {
                Intent data = (Intent) result[1];
                if(res==RESULT_OK){
                    try {
                        sv.logOut();
                        Toast.makeText(getApplicationContext(), "User "
                                        + data.getStringExtra("name") + " logged out",
                                Toast.LENGTH_LONG).show();
                    } catch (P2PhotoException e) {
                        Toast.makeText(getApplicationContext(), "Server side problem logging out",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else if(res==RESULT_CANCELED){
                    try {
                        sv.logOut();
                        Toast.makeText(getApplicationContext(), "User "
                                        + data.getStringExtra("name") + " logged out",
                                Toast.LENGTH_LONG).show();
                    } catch (P2PhotoException e) {
                        Toast.makeText(getApplicationContext(), "User " + user.getText().toString() + " logged out abruptly",
                                Toast.LENGTH_LONG).show();
                    }
                }
                user.setText("");
                pass.setText("");
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

    public static ServerConnector getSv(){
        return sv;
    }
}
