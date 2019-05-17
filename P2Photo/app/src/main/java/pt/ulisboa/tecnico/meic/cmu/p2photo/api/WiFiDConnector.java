package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.P2PhotoActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.bcastreceivers.P2PhotoWiFiDBroadcastReceiver;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.LocalCacheInit;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDIncommingMsg;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDSendMsg;

/**
 * A Class for implementing Wi-Fi Direct Support on P2Photo Application
 */
public class WiFiDConnector implements PeerListListener, GroupInfoListener {

    private static final String TAG = WiFiDConnector.class.getName();

    private static final String CLI_API_VERSION = "0.2";

    public enum MsgType {TEXT, B64FILE}

    private SimWifiP2pManager simWifiP2pManager;
    private SimWifiP2pSocketServer simWifiP2pSocketServer;
    private SimWifiP2pSocket simWifiP2pSocket;

    private P2PhotoWiFiDBroadcastReceiver p2PhotoWiFiDBroadcastReceiver;

    private P2PhotoActivity activity;

    private Channel channelService;
    private Messenger messengerService;
    private boolean mBound = false;

    private IntentFilter filter;

    /** Connector to P2PhotoServer **/
    private ServerConnector serverConnector;

    private WiFiDARP arpCache;

    public enum WiFiDP2PhotoOperation {GET_CATALOG, GET_PICTURE, WELCOME, INIT}

    /** API messages **/
    public static final String API_GET_CATALOG = "P2PHOTO GET-CATALOG %s %s %s";
    public static final String API_GET_PICTURE = "P2PHOTO GET-PICTURE %s \"%s\"";
    public static final String API_WELCOME = "P2PHOTO WELCOME %s %s";
    public static final String API_INIT = "P2PHOTO INIT %s";
    public static final String API_POST_CATALOG = "P2PHOTO POST-CATALOG %s %s";
    public static final String API_POST_PICTURE = "P2PHOTO POST-PICTURE %s %s";

    public WiFiDConnector(P2PhotoActivity activity, ServerConnector serverConnector) {
        this.activity = activity;
        this.serverConnector = serverConnector;
        this.arpCache = new WiFiDARP();

        //Init the WDSim API
        SimWifiP2pSocketManager.Init(activity.getApplicationContext());

        initBCastReceiver();

        try {
            simWifiP2pSocketServer = new SimWifiP2pSocketServer(10001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WiFiDARP getArpCache() {
        return arpCache;
    }

    public void initBCastReceiver() {
        //Init the Broadcast receiver
        this.filter = new IntentFilter();
        this.filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        this.filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        this.filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);

        p2PhotoWiFiDBroadcastReceiver = new P2PhotoWiFiDBroadcastReceiver(this.activity);
        registerBR(this.activity);
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        // compile list of network members
        StringBuilder peersStr = new StringBuilder();

        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);


            if (!arpCache.alreadySentInit(deviceName)) {
                arpCache.addSentInit(deviceName);
                String[] args = new String[1];
                args[0] = device.getVirtIp();
                //Send init to everybody to tell its current IP
                this.requestP2PhotoOperation(WiFiDConnector.WiFiDP2PhotoOperation.INIT, device.getVirtIp(), args);
            }

            //If INIT phase is over
            if (arpCache.resolve(Main.username) != null) {
                String[] args = new String[2];
                args[0] = Main.username;
                args[1] = arpCache.resolve(Main.username);
                //Send welcome to everybody
                this.requestP2PhotoOperation(WiFiDConnector.WiFiDP2PhotoOperation.WELCOME, device.getVirtIp(), args);
            }
            File userFolder = new File(activity.getFilesDir(), Main.username);
            new LocalCacheInit().execute(userFolder, Cache.getInstance());
        }

        // display list of network members
        /*new AlertDialog.Builder(activity)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();*/
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        // display list of devices in range
        new AlertDialog.Builder(activity)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            messengerService = new Messenger(service);
            simWifiP2pManager = new SimWifiP2pManager(messengerService);
            channelService = simWifiP2pManager.initialize(activity.getApplication(), activity.getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            messengerService = null;
            simWifiP2pManager = null;
            channelService = null;
            mBound = false;
        }
    };

    public void startBackgroundTask() {
        Log.i(TAG, "Started Background Task");

        Intent intent = new Intent(activity, SimWifiP2pService.class);
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mBound = true;
        new WiFiDIncommingMsg().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, simWifiP2pSocketServer);
    }

    public void sendMessage(String message, MsgType type, String ip) {
        Log.i(TAG, "Sending message through Wi-FiD: " + message);
        EditText debugIP = activity.findViewById(R.id.debugIP);
        String prefix;

        if (type == MsgType.TEXT)
            prefix = "MSG ";
        else if (type == MsgType.B64FILE)
            prefix = "B64F ";
        else prefix = "";


        new WiFiDSendMsg().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ip, prefix + message);
    }

    public void sendMessage(String message, String folderPath, String fileName, MsgType type, String ip) {
        Log.i(TAG, "Sending message through Wi-FiD: " + message);
        EditText debugIP = activity.findViewById(R.id.debugIP);
        String prefix;

        if (type == MsgType.TEXT)
            prefix = "MSG ";
        else if (type == MsgType.B64FILE)
            prefix = "B64F ";
        else prefix = "";


        new WiFiDSendMsg().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ip, prefix + fileName + " \"" + folderPath + "\" " + message);
    }

    public void sendMessage(String message, String fileName, MsgType type, String ip) {
        Log.i(TAG, "Sending message through Wi-FiD: " + message);
        EditText debugIP = activity.findViewById(R.id.debugIP);
        String prefix;

        if (type == MsgType.TEXT)
            prefix = "MSG ";
        else if (type == MsgType.B64FILE)
            prefix = "B64F ";
        else prefix = "";


        new WiFiDSendMsg().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ip, prefix + fileName + " \"\" " + message);
    }

    public void sendFile(String path2File, String ip) {
        sendFile("", path2File, ip, "N");
    }

    public void sendFile(String folderPath, String path2File, String ip, String mode) {
        try {
            File file = new File(path2File);
            String fileName;

            if (mode.equals("T")) {
                //DEBUG
                //fileName = "vitor.txt";
                fileName = Main.username + ".txt";
            } else {
                fileName = file.getName();
            }

            Cache.getInstance().clientLog.add("WiFID SEND FILE " + fileName + " to " + ip + " " + new Timestamp(System.currentTimeMillis()));

            FileInputStream fis = new FileInputStream(file);

            byte[] bytes = new byte[(int) file.length()];

            fis.read(bytes);
            fis.close();

            String base64Encode = Base64.encodeToString(bytes, Base64.NO_WRAP);

            if (folderPath == null || folderPath.equals(""))
                sendMessage(base64Encode, fileName, MsgType.B64FILE, ip);
            else sendMessage(base64Encode, folderPath, fileName, MsgType.B64FILE, ip);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void registerBR(P2PhotoActivity activity) {
        this.activity = activity;
        this.activity.registerReceiver(p2PhotoWiFiDBroadcastReceiver, this.filter);
    }

    public void stopBackgroundTask() {
        if (mBound) {
            activity.unbindService(mConnection);
            mBound = false;
            activity.unregisterReceiver(p2PhotoWiFiDBroadcastReceiver);
        }
    }

    public void unRegisterReceiver() {
        try {
            this.activity.unregisterReceiver(p2PhotoWiFiDBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "P2Photo broadcast is not registered here!");
        }
    }

    public void requestPeersInRange() {
        if (mBound) {
            simWifiP2pManager.requestPeers(channelService, this);
        } else {
            Toast.makeText(activity, "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void requestGroupInfo() {
        if (mBound) {
            simWifiP2pManager.requestGroupInfo(channelService, this);
        } else {
            Toast.makeText(activity, "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Request an operation to another peer.
     * Be aware that the operations are asynchronous, this means that you send this request and
     * the other peer will response as soon as possible. Delays may occur.
     * @param operation to be performed
     * @param args the arguments to be sent to the peer
     */
    public void requestP2PhotoOperation(WiFiDP2PhotoOperation operation, String ip, String... args) {
        switch (operation) {

            //inform other peer that i need a catalog
            case GET_CATALOG:
                Cache.getInstance().clientLog.add("WiFiD SEND GET-CATALOG of album "+ args[1] + " to " + ip + " " + new Timestamp(System.currentTimeMillis()));
                sendMessage(String.format(API_GET_CATALOG, args[0], args[1], args[2]), MsgType.TEXT, ip); break;
            case GET_PICTURE:
                Cache.getInstance().clientLog.add("WiFiD SEND GET-PICTURE of album "+ args[1] + " to " + ip + " " + new Timestamp(System.currentTimeMillis()));
                sendMessage(String.format(API_GET_PICTURE, args[0], args[1]), MsgType.TEXT, ip); break;
            case WELCOME:
                Cache.getInstance().clientLog.add("WiFiD SEND WELCOME to " + ip + " " + new Timestamp(System.currentTimeMillis()));
                sendMessage(String.format(API_WELCOME, args[0], args[1]), MsgType.TEXT, ip); break;
            case INIT:
                Cache.getInstance().clientLog.add("WiFiD SEND INIT to " + ip + " " + new Timestamp(System.currentTimeMillis()));
                sendMessage(String.format(API_INIT, args[0]), MsgType.TEXT, ip); break;
        }
    }

    public P2PhotoActivity getActivity() {
        return activity;
    }

    public void setActivity(P2PhotoActivity activity) {
        this.activity = activity;
    }
}
