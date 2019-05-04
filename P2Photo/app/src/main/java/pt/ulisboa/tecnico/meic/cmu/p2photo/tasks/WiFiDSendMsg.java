package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class WiFiDSendMsg extends AsyncTask<Object, String, Void> {
    private static final String TAG = WiFiDSendMsg.class.getName();


    @Override
    protected Void doInBackground(Object... params) {

        SimWifiP2pSocket mCliSocket = (SimWifiP2pSocket) params[0];
        String message = (String) params[1];

        try {
            mCliSocket.getOutputStream().write((message + "\n").getBytes());
            BufferedReader sockIn = new BufferedReader(
                    new InputStreamReader(mCliSocket.getInputStream()));
            sockIn.readLine();
            mCliSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //mTextInput.setText("");
        //guiUpdateDisconnectedState();
    }
}
