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
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.activities.ChooseCloudOrLocal.wifiConnector;

public class WiFiDIncommingMsg extends AsyncTask<Object, String, Void> {
    private static final String TAG = WiFiDIncommingMsg.class.getName();
    private static final int PORT = 10001;


    @Override
    protected Void doInBackground(Object... params) {
        SimWifiP2pSocketServer mSrvSocket = (SimWifiP2pSocketServer) params[0];

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
                    String[] folderPaths;
                    String prefix = null;
                    String content;
                    String folderPath;
                    String fileName;

                    line = sockIn.readLine();

                    receivedContent = line.split(" ");
                    folderPaths = line.split("\"");

                    prefix = receivedContent[0];
                    if (folderPaths.length > 1)
                        folderPath = folderPaths[1];
                    else folderPath = "";
                    fileName = receivedContent[1];
                    content = line.substring(line.indexOf(' ') + 1);

                    if (prefix.equals("MSG")) {
                        Log.d(TAG, "Received a message: " + content);

                        String commandArgs[] = content.split(" ");
                        String command = commandArgs[0];
                        if (command.equals("P2PHOTO")) {
                            String subCommand = commandArgs[1];
                            String username = commandArgs[2];
                            String arg = commandArgs[3];

                            switch (subCommand) {
                                case "GET-CATALOG":
                                    String mode = "N";
                                    if (commandArgs.length > 4) {
                                        mode = commandArgs[4];
                                    }

                                    //DEBUG
                                    //String userFolder = Main.sv.getAlbumOwner(Integer.parseInt(arg));
                                    String userFolder = Main.username;

                                    //Sends catalog of that album to another user
                                    String path2File = Main.DATA_FOLDER + "/" + userFolder + "/" +
                                            arg + "_catalog.txt";
                                    Log.d(TAG, "P2PHOTO GET-CATALOG path: " + path2File);

                                    String ip = wifiConnector.getArpCache().resolve(username);
                                    if (mode.equals("T"))
                                        wifiConnector.sendFile("tmp", path2File, ip, "T");
                                    else wifiConnector.sendFile(path2File, ip);

                                    break;

                                case "GET-PICTURE":
                                    //just to get album id without the first quote that encloses the path
                                    arg = arg.replace("\"", "");

                                    //DEBUG
                                    //userFolder = Main.sv.getAlbumOwner(Integer.parseInt(arg));
                                    userFolder = Main.username;

                                    String path = content.split("\"")[1];
                                    String folder = path.split("/")[0];
                                    //Sends catalog of that album to another user
                                    String path2Pic = Main.DATA_FOLDER + "/" + userFolder + "/" +
                                            path;
                                    Log.d(TAG, "P2PHOTO GET-PICTURE path: " + path2Pic);
                                    Log.d(TAG, "P2PHOTO GET-PICTURE folder: " + folder);
                                    ip = wifiConnector.getArpCache().resolve(username);


                                    wifiConnector.sendFile(folder, path2Pic, ip, "N");

                                    break;

                                case "WELCOME":
                                    //Store the data on P2Photo ARP cache
                                    wifiConnector.getArpCache().addEntry(username, commandArgs[3]);

                                    break;

                                case "INIT":
                                    //Special case of welcome where I save my own entry
                                    wifiConnector.getArpCache().addEntry(Main.username, commandArgs[2]);
                                    wifiConnector.requestGroupInfo();

                                    break;

                            }
                        }

                    } else if (prefix.equals("B64F")) {
                        String fileContent = content.substring(content.lastIndexOf(' ') + 1);
                        Log.d(TAG, "Received a file with name: " + fileName + " with content: " + fileContent);

                        //Write received bytes to a file
                        byte[] receivedBytes = Base64.decode(fileContent, Base64.NO_WRAP);

                        /*
                         path = Environment.getExternalStoragePublicDirectory(Main.CACHE_FOLDER + "/" + Main.username + "/" + folderPath);
                        else path = Environment.getExternalStoragePublicDirectory(Main.CACHE_FOLDER + "/" + Main.username);
                         */
                        File path;
                        if (folderPath != null && !folderPath.equals(""))
                            path = new File(Main.CACHE_FOLDER + "/" + Main.username + "/" + folderPath);
                        else path = new File(Main.CACHE_FOLDER + "/" + Main.username);

                        Log.d(TAG,path.getAbsolutePath());


                        path.mkdir();

                        File file = new File(path, fileName);
                        file.createNewFile();

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
