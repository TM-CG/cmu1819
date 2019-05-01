package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class LogOut extends AsyncTask<Object,Void,Object[]> {

    private Context context;
    private ServerConnector sv;
    private String user;

    @Override
    protected Object[] doInBackground(Object [] objects) {
        try {
            context = (Context) objects[0];
            user = (String) objects[1];
            sv = Main.sv;

            sv.logOut();
        } catch (P2PhotoException e) {
            Toast.makeText(context, "Server side problem logging out",
                    Toast.LENGTH_LONG).show();
        }
        return objects;
    }

    @Override
    protected void onPostExecute(Object[] result) {
        Integer res = (Integer) result[2];
        if(result != null) {
            Intent data = (Intent) result[3];
            if(res==RESULT_OK){
                try {
                    sv.logOut();
                    Toast.makeText(context, "User "
                                    + data.getStringExtra("name") + " logged out",
                            Toast.LENGTH_LONG).show();
                } catch (P2PhotoException e) {
                    Toast.makeText(context, "User "
                                    + data.getStringExtra("name") + " logged out",
                            Toast.LENGTH_LONG).show();
                }
            }
            else if(res==RESULT_CANCELED){
                try {
                    sv.logOut();
                    Toast.makeText(context, "User "
                                    + data.getStringExtra("name") + " logged out",
                            Toast.LENGTH_LONG).show();
                } catch (P2PhotoException e) {
                    Toast.makeText(context, "User " + user + " logged out abruptly",
                            Toast.LENGTH_LONG).show();
                }
            }
            //user.setText("");
            //pass.setText("");
        }

    }
}
