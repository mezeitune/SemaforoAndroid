package com.adox.semacc.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.adox.semacc.udp.UdpClient;
import com.adox.semacc.udp.UdpDataHandler;
import com.adox.semacc.util.Util;

import java.util.HashMap;


/**
 * Comunicacion entre la aplicacion y un semáforo en particular
 */
public class SemComunication extends BroadcastReceiver implements Runnable, UdpDataHandler,
        TextToSpeech.OnInitListener {

    private static int networkId;
    private static String currentSSID;
    private static HashMap<String, SemComunication> currentComunications = new HashMap<String, SemComunication>();
    public static boolean run = true;
    private TextToSpeech textToSpeech;
    private boolean running = true;
    private WifiManager wifi;
    private WifiConfiguration conf;
    private Context context;
    private String SSID;
    public  UdpClient client;

    private static final int TIEMPO_AVISO = 5;

    // Tiempos y datos del cruce
    int tiempoVerde = 0;
    int tiempoRojo = 0;
    int tiempoDisponible = 0;
    int lastAlert = 100;
    String calleRojo = "";
    String calleVerde = "";
    boolean reproducirCompleto = true;
    private boolean continueAskingTimes = true;
    private boolean canCross = false;

    public static void create(String SSID, Context context) {

        if (!SSID.equals(SemComunication.currentSSID)) {

            SemComunication comunication = new SemComunication();

            comunication.wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            comunication.context = context;
            comunication.SSID = SSID;

            new Thread(comunication).start();

        }
    }


    @Override
    public void run() {
        this.textToSpeech = new TextToSpeech(context, this);
        this.textToSpeech.setSpeechRate(2);
        createClient();
        while(true) {
            requestTimes();
            SystemClock.sleep(3000);
        }

    }
    /**
     * Implementacion del metodo que recibe cambios en el estado de la señal
     */
    @Override
    public void onReceive(Context context, Intent intent) {


    }

    /**
     * Crea el cliente TCP, y lo conecta
     */
    public void createClient() {
        client = new UdpClient(Util.semIp, Util.semPort, this);

        // Le paso true porque quiero que lo haga en un hilo nuevo
        client.connect(true);
    }

    /**
     * Cierra todas las conexiones y elimina el cliente
     */
    public void close() {

        this.client.close();

    }

    /**
     * Este método se ejecuta cuando el socket ya esta conectado,
     * por lo tanto hay conexión total con el semáforo
     */
    @Override
    public void onConected(boolean ok) {

    }

    /**
     * Estoy conectado y con el sintetizador iniciado
     */
    @Override
    public void onInit(int i) {


    }



    /**
     * Una vez encontrado y conectado el semáforo, se solicita la informacion completa
     */
    protected void requestTimes() {

        Log.i("#FSEM# SERVICE", "REQUEST TIME");

        client.send("<TIEMPO>");

    }


    /**
     * Llega un dato del semáforo por UDP
     * @param data
     */
    @Override
    public void onDataRead(String data) {
        Log.i("#FSEM# SERVICE", "READ: " + data);
        if(!Util.aplicacionPausada){
            if (data == null) {
                data = "";
            }

            data = data.substring(data.indexOf('<') + 1, data.indexOf('>'));

            char first = data.charAt(0);
            String tiempo=data.substring(1, 4);
            String calle=data.substring(4,data.length());
            Log.i("#FSEM# SERVICE", tiempo + "+" + calle);
            try{
                int tiempoo = Integer.parseInt(tiempo);
                if("R".equals(String.valueOf(first))){
                    Log.i("#FSEM# SERVICE","rojo");
                    vibrarNoCruzar();
                    textToSpeech.speak("Espere "+tiempoo+"segundos para cruzar "+calle, TextToSpeech.QUEUE_FLUSH, null);
                }else if("V".equals(String.valueOf(first))){
                    Log.i("#FSEM# SERVICE", "verde");
                    vibrarCruzar();
                    textToSpeech.speak("Tiene "+tiempoo+"segundos para cruzar "+calle, TextToSpeech.QUEUE_FLUSH, null);
                }else if("A".equals(String.valueOf(first))){
                    Log.i("#FSEM# SERVICE", "amarillo");
                    vibrarNoCruzar();
                    textToSpeech.speak("Ultimos "+tiempoo+"segundos ", TextToSpeech.QUEUE_FLUSH, null);
                }
            }catch (Exception e) {
                    e.printStackTrace();
            }
        }

    }





    private void vibrarNoCruzar(){
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(300);
    }

    private void vibrarCruzar(){
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
       // v.vibrate(1500);
        long[] pattern = new long[11];
        pattern[0] = 500; // Wait one second
        pattern[1] = 500;  // Vibrate for most a second
        pattern[2] = 500;   // A pause long enough to feel distinction
        pattern[3] = 500;  // Repeat 3 more times
        pattern[4] = 500;
        pattern[5] = 500;
        pattern[6] = 500;
        pattern[7] = 500;
        pattern[8] = 500;
        pattern[9] = 500;
        pattern[10] = 500;
        v.vibrate(pattern, -1);
    }

}