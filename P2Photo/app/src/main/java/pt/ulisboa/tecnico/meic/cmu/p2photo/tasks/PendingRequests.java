package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

public class PendingRequests extends AsyncTask<Object,Void,Object[]> {
    @Override
    protected Object[] doInBackground(Object [] objects) {
        Object[] result = new Object[2];
        result[0] = objects[0];
        try {
            List<Integer> requests = Main.getSv().listIncomingRequest();
            result[1] = requests;
            return result;
        } catch (P2PhotoException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object[] result) {
        if(result != null){
            ArrayAdapter<String> itemsAdapter = (ArrayAdapter<String>) result[0];
            Log.d("users", Main.getUser());
            for(Integer id: (List<Integer>) result[1]){
                Log.d("ids", String.valueOf(id));
                itemsAdapter.add(String.valueOf(id));
            }
        }

    }
}
