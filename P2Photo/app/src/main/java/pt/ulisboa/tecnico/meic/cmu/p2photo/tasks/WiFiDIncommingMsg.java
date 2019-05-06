package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;

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
                    String line;
                    String[] receivedContent;
                    String prefix = null;
                    String content;
                    String fileName;

                    line = sockIn.readLine();

                    receivedContent = line.split(" ");

                    prefix = receivedContent[0];
                    fileName = receivedContent[1];
                    content = line.substring(line.indexOf(' ',line.indexOf(' ') + 1) + 1);

                    if (prefix.equals("MSG")) {
                        Log.d(TAG, "Received a message: " + content);

                    } else if (prefix.equals("B64F")) {
                        Log.d(TAG, "Received a file with name: " + fileName + " with content: " + content);

                        //Write received bytes to a file
                        byte[] receivedBytes = Base64.decode(content, Base64.NO_WRAP);
                        Log.d(TAG,Environment.getExternalStoragePublicDirectory(Main.CACHE_FOLDER + "/" + Main.username) + "/" + fileName);
                        File path = Environment.getExternalStoragePublicDirectory(Main.CACHE_FOLDER + "/" + Main.username);
                        path.mkdir();

                        File file = new File(path, fileName);

                        FileOutputStream fos = new FileOutputStream(file);

                        fos.write(receivedBytes);

                        fos.close();
                    }

                    publishProgress(content);
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
