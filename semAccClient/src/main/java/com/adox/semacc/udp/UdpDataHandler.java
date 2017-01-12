package com.adox.semacc.udp;

/**
 * Interfaz para recibir datos por TCP
 */
public interface UdpDataHandler {
    void onDataRead(String data);
    void onConected(boolean ok);
}
