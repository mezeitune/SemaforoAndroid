package com.adox.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author aarias
 */
public abstract class HTTPServer extends Thread {

    private Integer puerto = null;

    /**
     * Metodo que se ejecuta cuando llega una peticion HTTP. Se debe reemplazar
     * por una implementacion propia
     */
    protected abstract String procesarAccion(HashMap<String, String> params);

    /**
     * El metodo crea un Socket TCP. Da por asumida la existencia de la
     * propertie PUERTOTCP. Para cambiar el puerto manualmente se debe utilizar
     * setPuerto();
     */
    @Override
    public void run() {
        try {
        	
            HashMap<String, String> params;

            ServerSocket serverSocket = new ServerSocket(puerto);

            while (true) {
                try {
                    // Recibo una nueva peticion.
                    Socket conexion = serverSocket.accept();

                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    DataOutputStream out = new DataOutputStream(conexion.getOutputStream());

                    params = obtGETParams(in);

                    String response = this.procesarAccion(params);

                    String headers = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/html\r\n"
                            + "Content-Length: " + response.length() + "\r\n"
                            + "\r\n";

                    out.writeBytes(headers + response);
                    conexion.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Toma los parametros GET de un request.
     *
     * @param in BufferedReader del socket TCP del request.
     * @return Un HashMap<String, String> con los valores de los parametros.
     * @throws IOException
     */
    public HashMap<String, String> obtGETParams(BufferedReader in) throws IOException {

        String data = "";
        HashMap<String, String> params = new HashMap<String, String>();

        try {

            while (data.indexOf("GET") == -1) {
                data = in.readLine();
            }
            data = data.substring(data.indexOf("?") + 1, data.lastIndexOf(" "));

            while (data.indexOf("=") != -1) {

                String key = data.substring(0, data.indexOf("="));
                String val;
                if (data.indexOf("&") != -1) {
                    val = data.substring(data.indexOf("=") + 1, data.indexOf("&"));
                    data = data.substring(data.indexOf("&") + 1);
                } else {
                    val = data.substring(data.indexOf("=") + 1);
                    data = "";
                }
                params.put(key, val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;

    }

    public Integer getPuerto() {
        return puerto;
    }

    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }
}
