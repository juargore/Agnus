package com.proj.agnus.conexion;

import android.app.Activity;
import android.util.Log;

import org.apache.commons.collections4.MultiMap;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Arturo on 2/10/2018.
 */

public class connect_HttpsPost {

    URL url;
    StringBuilder output;
    JSONObject jsonRespuesta;


    public JSONObject connect(String url_entrada, HashMap<String, String> listado, Activity context){

        check_internet_connection check = new check_internet_connection();
        if(check.isConnected(context)){
            try {
                if(url_entrada.contains("http://")){
                    url_entrada = url_entrada.replace("http://","https://");
                    url_entrada = url_entrada.replace(" ","%20");
                } else if (url_entrada.contains("https://")){
                    url_entrada = url_entrada.replace(" ","%20");
                } else {
                    url_entrada = "https://"+url_entrada.replace(" ","%20");
                }

                url = new URL(url_entrada);

                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

                String urlParameters = "";
                Set keys = listado.keySet();

                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    String key = (String) i.next();
                    String value = listado.get(key);

                    Log.e(""+key,""+value);
                    urlParameters = urlParameters + key + "=" + ""+ URLEncoder.encode(value.replace("%20", " ").replace("%0A", "<br>"), "UTF-8") + "&";
                }

                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters.substring(0, urlParameters.length() - 1));
                dStream.flush();
                dStream.close();

                int responseCode = connection.getResponseCode();
                output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") +
                        "Response " + System.getProperty("line.separator") +
                        System.getProperty("line.separator") + responseOutput.toString());

                jsonRespuesta = new JSONObject(responseOutput.toString());
                Log.e("RES", ""+jsonRespuesta);

            } catch (MalformedURLException e) {
                // TODO
                e.printStackTrace();
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonRespuesta;
        } else {
            //check.MsgInternet(context);
            Log.e("--","No hay internet");


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("status", "fail");
                jsonObject.put("mensaje", "No hay conexión a internet");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    public JSONObject connect(String url_entrada, String urlParameters, Activity context){

        //Revisar conexion a internet
        check_internet_connection check = new check_internet_connection();

        if(check.isConnected(context)){
            try {
                //Asegurar el https del link
                if(url_entrada.contains("http://")){
                    url_entrada = url_entrada.replace("http://","https://");
                    url_entrada = url_entrada.replace(" ","%20");
                } else if (url_entrada.contains("https://")){
                    url_entrada = url_entrada.replace(" ","%20");
                } else {
                    url_entrada = "https://"+url_entrada.replace(" ","%20");
                }

                url = new URL(url_entrada);

                //Configurar Https
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters.substring(0, urlParameters.length() - 1));
                dStream.flush();
                dStream.close();

                int responseCode = connection.getResponseCode();
                output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") +
                        "Response " + System.getProperty("line.separator") +
                        System.getProperty("line.separator") + responseOutput.toString());

                jsonRespuesta = new JSONObject(responseOutput.toString());
                Log.e("RES", ""+jsonRespuesta);

            } catch (MalformedURLException e) {
                // TODO
                e.printStackTrace();
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonRespuesta;
        } else {
            //check.MsgInternet(context);
            Log.e("--","No hay internet");


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("status", "fail");
                jsonObject.put("mensaje", "No hay conexión a internet");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }


}
