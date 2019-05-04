package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class WiFiDOutgoingMsg extends AsyncTask<Object, Void, String> {

    private static final int PORT = 10001;

    @Override
    protected void onPreExecute() {
        //mTextOutput.setText("Connecting...");
    }

    @Override
    protected String doInBackground(Object... params) {
        SimWifiP2pSocket mCliSocket = (SimWifiP2pSocket) params[0];
        String message = (String) params[1];

        try {
            mCliSocket = new SimWifiP2pSocket(message, PORT);
        } catch (UnknownHostException e) {
            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            return "IO error:" + e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        /*if (result != null) {
            guiUpdateDisconnectedState();
            mTextOutput.setText(result);
        } else {
            findViewById(R.id.idDisconnectButton).setEnabled(true);
            findViewById(R.id.idConnectButton).setEnabled(false);
            findViewById(R.id.idSendButton).setEnabled(true);
            mTextInput.setHint("");
            mTextInput.setText("");
            mTextOutput.setText("");
        }*/
    }
}
