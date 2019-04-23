package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity.sv;

public class SignOut extends AsyncTask {
    @Override
    protected String doInBackground(Object[] objects) {
        try {
            sv.logOut();
            return "OK";
        } catch (P2PhotoException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(Object result) {

    }
}
