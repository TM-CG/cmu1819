package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.p2photo.MainActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
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
        sv = MainActivity.sv;
        EditText ip = (EditText) objects[2];
        EditText port = (EditText) objects[3];
        user = (String) objects[4];
        pass = (String) objects[5];

        try {
            ServerConnector tmp = new ServerConnector(ip.getText().toString(), Integer.parseInt(port.getText().toString()));
            result[1] =  tmp;
            return result;
        } catch (P2PhotoException e) {
            result[1] =  null;
            return result;
        }
    }

    @Override
    protected void onPostExecute(Object[] result) {
        if(result[1] == null) {
            sv = null;
            Toast.makeText(context, "Connection not established",
                    Toast.LENGTH_LONG).show();
        }
        else{
            sv = (ServerConnector) result[1];
            if(result[0] == "signIn"){
                //if (checkArguments()) {
                    new SignIn().execute(user, pass);
               // }
            }
            else if(result[0] == "signUp"){
                //if (checkArguments()) {
                    new SignUp().execute();
               // }
            }
        }
    }

    /*private boolean checkArguments(){
        user = (EditText) findViewById(R.id.textUser);
        pass = (EditText) findViewById(R.id.textPass);
        intent.putExtra("name", user.getText().toString());
        intent.putExtra("pass", pass.getText().toString());

        setResult(RESULT_OK, intent);

        if((user.getText().toString().matches("")) || pass.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Name or password invalid",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }*/
}
