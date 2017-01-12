package com.adox.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpCall extends Thread {// a esta clase , la extiendo de thread
										// para que se ejecute en paralelo

	public enum CallType {POST, GET};
	
	private String accion;
	private String posturl;
	private String response;
	private CallType type = CallType.POST;

	private HttpCallCallbackHandler callbackHandler;

	private HashMap<String, String> paramsMap = null;
	private HashMap<String, String> responseMap = null;

	public HttpCall(String posturl, String a,
			HttpCallCallbackHandler callbackHandler) {
		this(posturl, a, callbackHandler, CallType.POST);
	}
	
	public HttpCall(String posturl, String a,
			HttpCallCallbackHandler callbackHandler, CallType type) {
		this.accion = a;
		this.posturl = posturl;
		this.callbackHandler = callbackHandler;
		this.paramsMap = new HashMap<String, String>();
		this.type = type;
	}


	public void addParam(String k, String v) {
		this.paramsMap.put(k, v);
	}

	public void run(){
				
		if(this.type == CallType.POST){
			runPost();
		}else if(this.type == CallType.GET){
			runGet();
		}
	}
	
	public void runGet() {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet();
			String fullUrl = posturl + "?acc=" + this.accion;
			
			
			for (String k : paramsMap.keySet()) {
				fullUrl += "&" + k + "=" + paramsMap.get(k);
			}
			
			httpget.setURI(new URI(fullUrl));
			HttpResponse resp = httpclient.execute(httpget);
			HttpEntity ent = resp.getEntity();
			this.response = EntityUtils.toString(ent);

			// Muestro respuesta
			System.out.println(response);

			// Parseo respuesta en HashMap
			this.responseMap = parseResponse(response);

			if (this.callbackHandler != null)
				this.callbackHandler.onResponse(this.accion, this.responseMap,
						this.paramsMap);
		} catch (Exception e) {
			e.printStackTrace();
			this.response = "error";
		}
	}

	public void runPost() {

		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(posturl);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Ingreso parametros
			params.add(new BasicNameValuePair("acc", this.accion));
			for (String k : paramsMap.keySet()) {
				params.add(new BasicNameValuePair(k, paramsMap.get(k)));
			}

			// Ejecuto el Request
			httppost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse resp = httpclient.execute(httppost);
			HttpEntity ent = resp.getEntity();
			this.response = EntityUtils.toString(ent);

			// Muestro respuesta
			System.out.println(response);

			// Parseo respuesta en HashMap
			this.responseMap = parseResponse(response);

			if (this.callbackHandler != null)
				this.callbackHandler.onResponse(this.accion, this.responseMap,
						this.paramsMap);

		} catch (Exception e) {
			e.printStackTrace();
			this.response = "error";
		}

	}

	/**
	 * Transforma un string del tipo: "campo=valor;campo=valor" En un hashmap
	 * <campo, valor>.
	 * */
	private HashMap<String, String> parseResponse(String respStr) {

		HashMap<String, String> respMap = new HashMap<String, String>();
		String[] respArr = respStr.split(";");

		Log.i("ADOXLIB", String.valueOf(respStr.length()));
		
		for (String field : respArr) {
			String[] fieldArr = field.split("=");
			if (fieldArr.length == 2)
				respMap.put(fieldArr[0].trim(), fieldArr[1].trim());
		}

		return respMap;
	}

	/**
	 * Espera hasta que una respuesta exista.
	 * 
	 * Este metodo esta mal implementado, ya que es bloqueante y no pasivo por
	 * lo que no debe utilizarse en general, y menos en hilos principales de
	 * ejecucion.
	 * 
	 * Se debe implementar la interfaz {@link HttpCallCallbackHandler}, y pasar
	 * dicho handler como parametro en la inicializacion de la llamada.
	 * */
	public HashMap<String, String> getResponse() {
		String resp = null;
		while (this.response == null)
			try {
				currentThread();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		resp = this.response;

		this.response = null;

		return this.parseResponse(resp);
	}

	public CallType getType() {
		return type;
	}

	public void setType(CallType type) {
		this.type = type;
	}
	
	
}
