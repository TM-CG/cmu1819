package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

public class FindUsers extends AsyncTask<Object,Void,Object[]> {
    @Override
    protected Object[] doInBackground(Object [] objects) {
        Object[] result = new Object[2];
        result[0] = objects[0];
        try {
            List<String> users = MainActivity.getSv().findUsers("*");
            result[1] = users;
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
            Log.d("users", MainActivity.getUser());
            for(String user: (List<String>) result[1]){
                if(!MainActivity.getUser().equals(user)) {
                    Log.d("users", user);
                    itemsAdapter.add(user);
                }
            }
        }

    }
}
