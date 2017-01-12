package com.adox.semacc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.adox.semacc.MainActivity;
import com.adox.semacc.R;
import com.adox.semacc.util.Util;

import java.util.List;

/**
 * Servicio de busqueda de semáforos compatibles
 */
public class FindSemService extends IntentService{

	// Constructor del servicio
	public FindSemService() {
		super("FindSemService");
	}

	/**
	 * Devuelve START_STICKY para no desligar el servicio */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(" #FSEM# SERVICE", "onStartCommand FindSemService");
		return START_STICKY;
	}

	/**
	 * onCreate - Se ejecuta solo si no está creado, obtiene el WifiManager,
	 * crea el lock e inicia el hilo de recepción de resultados
	 * de SSID WiFi disponibles */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("#FSEM#  SERVICE", "onCreate FindSemService");

		MainActivity.ConnectToWiFi("InfoSign", "adox2311", getApplicationContext());
	}



	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("#FSEM# SERVICE", "onHandleIntent FindSignService");
	}

	@Override
	public void onDestroy() {
		Log.i("#FSEM# SERVICE", "onDestroy FindSignService");
		super.onDestroy();
	}

}