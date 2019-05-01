package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

/*
 * Async call for rejecting invitations requests
 */
public class RejectPendingInvitation  extends AsyncTask<Object, Object, Object[]> {
    private ServerConnector sv = Main.sv;

    @Override
    protected void onPostExecute(Object[] o) {
    }

    @Override
    protected Object[] doInBackground(Object[] objects) {
        String id = (String) objects[0];
        try {
            sv.rejectIncomingRequest(Integer.parseInt(id));
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }
        return null;
    }
}
