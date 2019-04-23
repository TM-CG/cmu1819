package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class SignIn extends AsyncTask<String, String[], String> {

    private ServerConnector sv;
    private String user;
    private String pass;

    @Override
    protected String doInBackground(String [] objects) {
        try {
            sv = MainActivity.sv;
            user = objects[0];
            pass = objects[1];

            sv.logIn(user, pass);

            //startActivityForResult(intent, 2);
            return "OK";
        } catch (P2PhotoException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        String msg = result;
        //doToast(msg);

    }

}
