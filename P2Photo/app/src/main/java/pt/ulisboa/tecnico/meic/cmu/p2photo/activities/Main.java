package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.LogOut;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.SocketConnect;

public class Main extends AppCompatActivity {

    private static final int WRITE_PERMISSION = 1;


    private Intent  intent;
    private EditText user;
    private EditText pass;
    private EditText ip;
    private EditText port;
    public static ServerConnector sv;

    /** The username of the current user **/
    public static String username;

    /** Temporary location to store catalogs and pictures **/
    public static String CACHE_FOLDER;

    public static String getUser() {
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Storage permission to the user
        int storageWritePermisson = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storageReadPermisson = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (storageWritePermisson == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_PERMISSION);
        }
        user = findViewById(R.id.textUser);
        pass = findViewById(R.id.textPass);

        //initialize cache folder
        CACHE_FOLDER = getApplicationContext().getCacheDir().getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do nothing in case of permission granted

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("We need to store temporary files in order to improve your experience. This app will exit now.")
                            .setTitle("Storage needed!");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Main.this.finish();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

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
            //store the username globally
            username = user.getText().toString();

            intent = new Intent(this, ChooseCloudOrLocal.class);
            new SocketConnect().execute("signIn", getApplicationContext(), ip.getText().toString(),
                    port.getText().toString(), user.getText().toString(), pass.getText().toString());
            startActivity(intent);
        }
    }

    public void signUp(View view) {
        //vitor: just for testing read and write of temp files
        /*AlbumCatalog catalog = new AlbumCatalog(1, "Album do zé");

        new Thread(new CloudStorage(this, catalog, StorageProvider.Operation.WRITE), "WritingThread").start();

        CloudStorage cs = new CloudStorage(this, null, StorageProvider.Operation.READ);
        new Thread(cs, "ReadingThread").start();*/

        if(checkConnectionParameters()){
            //store the username globally
            username = user.getText().toString();

            intent = new Intent(this, ChooseCloudOrLocal.class);
            new SocketConnect().execute("signUp", getApplicationContext(), ip.getText().toString(),
                    port.getText().toString(), user.getText().toString(), pass.getText().toString());
            startActivity(intent);
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
                new LogOut().execute(getApplicationContext(), user.getText().toString(), resultCode,data);
                break;
        }
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
