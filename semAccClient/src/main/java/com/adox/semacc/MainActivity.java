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
import android.widget.Button;

import com.adox.semacc.service.SemComunication;
import com.adox.semacc.udp.UdpDataHandler;
import com.adox.semacc.util.Util;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener {

    protected SharedPreferences prefs;
    PendingIntent pintent;
    AlarmManager alarmManager;

    private Button btnPausar;
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
    private int pausadoCounter=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textToSpeech = new TextToSpeech(getApplicationContext(), (TextToSpeech.OnInitListener) this);
        this.textToSpeech.setSpeechRate(2);
        if(!Util.aplicacionPausada){
            Util.ConnectToWiFi("InfoSign", "adox2311", getApplicationContext());

            SemComunication.create("InfoSign", getApplicationContext());
        }


        btnPausar = (Button) findViewById(R.id.btnPausar);
        btnPausar.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        if (pausadoCounter % 2 == 0) {
                            btnPausar.setText("Aplicacion Reanudada");

                            Util.aplicacionPausada = false;
                            textToSpeech.speak("Aplicacion Reanudada", TextToSpeech.QUEUE_FLUSH, null);
                            Util.ConnectToWiFi("InfoSign", "adox2311", getApplicationContext());

                            pausadoCounter += 1;
                        } else {
                            btnPausar.setText("Aplicacion Pausada");
                            Util.olvidarInfoSign("InfoSign", "adox2311", getApplicationContext());
                            Util.aplicacionPausada = true;
                            textToSpeech.speak("Aplicacion Pausada", TextToSpeech.QUEUE_FLUSH, null);
                            pausadoCounter += 1;
                        }

                    }
                });


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

    @Override
    public void onInit(int i) {

    }

    public enum StaticState {
        CONECTADO, NO_CONECTADO
    }
}
