package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

public class WiFiDIncommingMsg extends AsyncTask<SimWifiP2pSocketServer, String, Void> {
    private static final String TAG = WiFiDIncommingMsg.class.getName();
    private static final int PORT = 10001;


    @Override
    protected Void doInBackground(SimWifiP2pSocketServer... params) {
        SimWifiP2pSocketServer mSrvSocket = params[0];

        Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

        while (!Thread.currentThread().isInterrupted()) {
            Log.d(TAG, "Before accept (" + this.hashCode() + ").");
            try {
                SimWifiP2pSocket sock = mSrvSocket.accept();
                Log.d(TAG, "after accept (" + this.hashCode() + ").");

                try {
                    BufferedReader sockIn = new BufferedReader(
                            new InputStreamReader(sock.getInputStream()));
                    String st = sockIn.readLine();

                    String[] receivedContent = st.split(" ");

                    String prefix = receivedContent[0];
                    String content = st.substring(st.indexOf(' '));

                    if (prefix.equals("MSG")){
                        Log.d(TAG, "Received a message: " + content);

                    } else if (prefix.equals("B64F")) {
                        Log.d(TAG, "Received a file: " + content);

                        //TODO: Store the file somewhere in internal storage
                    }

                    publishProgress(st);
                    sock.getOutputStream().write(("\n").getBytes());
                } catch (IOException e) {
                    Log.d("Error reading socket:", e.getMessage());
                } finally {
                    sock.close();
                }
            } catch (IOException e) {
                Log.d("Error socket:", e.getMessage());
                break;
                //e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //mTextOutput.append(values[0] + "\n");
    }
}
