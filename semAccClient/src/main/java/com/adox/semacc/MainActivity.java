package com.adox.semacc;


import com.adox.semacc.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adox.semacc.util.Util;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    protected SharedPreferences prefs;
    PendingIntent pintent;
    AlarmManager alarmManager;


    private static int networkId;
    private static String currentSSID;
    public static StaticState staticState = StaticState.NO_CONECTADO;
    public static boolean run = true;
    private TextToSpeech textToSpeech;
    private boolean running = true;
    private WifiManager wifi;
    private WifiConfiguration conf;
    private Context context;
    private String SSID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectToWiFi("InfoSign","adox2311", getApplicationContext());

    }

    static public void ConnectToWiFi(String ssid ,String key,Context ctx) {

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(ctx.WIFI_SERVICE);
        int networkId = wifiManager.getConnectionInfo().getNetworkId();
        wifiManager.removeNetwork(networkId);
        wifiManager.saveConfiguration();
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    public void onClickClose(View v) {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(" #FSEM# ACTIVITY", "onDestroy MainActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public enum StaticState {
        CONECTADO, NO_CONECTADO
    }
}
