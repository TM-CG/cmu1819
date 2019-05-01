package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class LogIn extends AsyncTask<String, String[], String> {

    private ServerConnector sv;
    private String user;
    private String pass;

    @Override
    protected String doInBackground(String [] params) {
        try {
            sv = Main.sv;
            user = params[0];
            pass = params[1];

            sv.logIn(user, pass);

            //startActivityForResult(intent, 2);
            return "OK";
        } catch (P2PhotoException e) {
            return e.getMessage();
        }
    }

}
