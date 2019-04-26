package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class SignUp extends AsyncTask {

    private ServerConnector sv;
    private String user;
    private String pass;

    @Override
    protected String doInBackground(Object [] objects) {
        try {
            sv = Main.sv;
            user = (String) objects[0];
            pass = (String) objects[1];

            sv.signUp(user, pass);
            new SignIn().execute();
            return "OK";
        } catch (P2PhotoException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        String msg = (String) result;
        //doToast(msg);

    }

}
