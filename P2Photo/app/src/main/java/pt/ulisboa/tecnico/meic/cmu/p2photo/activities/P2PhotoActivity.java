package pt.ulisboa.tecnico.meic.cmu.p2photo.activities;

import android.os.Bundle;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.WiFiDConnector;

public abstract class P2PhotoActivity extends DropboxActivity {

    public static WiFiDConnector wifiConnector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWiFiDConnector();
    }

    public void initWiFiDConnector() {
        if (Main.STORAGE_TYPE == Main.StorageType.LOCAL) {
            if (wifiConnector == null) {
                wifiConnector = new WiFiDConnector(this, Main.sv);
                wifiConnector.startBackgroundTask();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wifiConnector != null)
            wifiConnector.unRegisterReceiver();
    }

    @Override
    protected void loadData() {

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (wifiConnector == null) {

            return;
        }
        wifiConnector.registerBR(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiConnector.unRegisterReceiver();
    }*/
}
