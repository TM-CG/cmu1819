package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class SocketConnect extends AsyncTask<Object,Void,Object[]> {

    private Context context;
    private ServerConnector sv;
    private String user;
    private String pass;

    @Override
    protected Object[] doInBackground(Object [] objects) {
        Object[] result = new Object[2];
        result[0] = objects[0];

        context = (Context) objects[1];
        sv = Main.sv;
        String ip = (String) objects[2];
        String port = (String) objects[3];
        user = (String) objects[4];
        pass = (String) objects[5];

        try {
            Main.sv = new ServerConnector(ip, Integer.parseInt(port));
            result[1] =  Main.sv;
            return result;
        } catch (P2PhotoException e) {
            result[1] =  null;
            return result;
        }
    }

    /*@Override
    protected void onPostExecute(Object[] result) {
        if(result[1] == null) {
            sv = null;
            Toast.makeText(context, "Connection not established",
                    Toast.LENGTH_LONG).show();
        }
        else{
            sv = (ServerConnector) result[1];
            if(result[0] == "logIn"){
                new LogIn().execute(user, pass);
            }
            else if(result[0] == "signUp"){
                new SignUp().execute(user, pass);
            }
        }
    }*/
}
