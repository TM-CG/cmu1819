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

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.bcastreceivers.P2PhotoWiFiDBroadcastReceiver;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDIncommingMsg;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.WiFiDSendMsg;

/**
 * A Class for implementing Wi-Fi Direct Support on P2Photo Application
 */
public class WiFiDConnector implements PeerListListener, GroupInfoListener {

    private static final String TAG = WiFiDConnector.class.getName();

    public enum MsgType {TEXT, B64FILE}

    private SimWifiP2pManager simWifiP2pManager;
    private SimWifiP2pSocketServer simWifiP2pSocketServer;
    private SimWifiP2pSocket simWifiP2pSocket;

    private P2PhotoWiFiDBroadcastReceiver p2PhotoWiFiDBroadcastReceiver;

    private AppCompatActivity activity;

    private Channel channelService;
    private Messenger messengerService;
    private boolean mBound = false;

    /** Connector to P2PhotoServer **/
    private ServerConnector serverConnector;

    public WiFiDConnector(AppCompatActivity activity, ServerConnector serverConnector) {
        this.activity = activity;
        this.serverConnector = serverConnector;

        //Init the WDSim API
        SimWifiP2pSocketManager.Init(activity.getApplicationContext());

        initBCastReceiver();

        try {
            simWifiP2pSocketServer = new SimWifiP2pSocketServer(10001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initBCastReceiver() {
        //Init the Broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);

        p2PhotoWiFiDBroadcastReceiver = new P2PhotoWiFiDBroadcastReceiver(this.activity);
        this.activity.registerReceiver(p2PhotoWiFiDBroadcastReceiver, filter);
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
        }

        // display list of network members
        new AlertDialog.Builder(activity)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
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

    public void sendMessage(String message, MsgType type) {
        Log.i(TAG, "Sending message through Wi-FiD: " + message);
        EditText debugIP = activity.findViewById(R.id.debugIP);
        String prefix;

        if (type == MsgType.TEXT)
            prefix = "MSG ";
        else if (type == MsgType.B64FILE)
            prefix = "B64F ";
        else prefix = "";


        new WiFiDSendMsg().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, debugIP.getText().toString(), prefix + message);
    }

    public void sendFile(String path2File) {
        try {
            File file = new File(path2File);
            FileInputStream fis = new FileInputStream(file);

            byte[] bytes = new byte[(int) file.length()];

            fis.read(bytes);
            fis.close();

            String base64Encode = Base64.encodeToString(bytes, Base64.NO_WRAP);

            sendMessage(base64Encode, MsgType.B64FILE);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void stopBackgroundTask() {
        if (mBound) {
            activity.unbindService(mConnection);
            mBound = false;
            activity.unregisterReceiver(p2PhotoWiFiDBroadcastReceiver);
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
}
