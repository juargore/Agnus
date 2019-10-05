package com.proj.agnus.conexion;

import android.app.Activity;
import android.os.StrictMode;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Arturo on 2/10/2018.
 */

public class connect_HttpPost {

    String responseServer, url;
    JSONObject jsonRespuesta;
    connect_HttpsPost conectar;

    public JSONObject connect(String url, HashMap<String, String> listado, Activity context){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        conectar = new connect_HttpsPost();
        jsonRespuesta = conectar.connect(url, listado,context);

        /*this.url = url;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://"+url);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Set keys = listado.keySet();

            for (Iterator i = keys.iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                String value = listado.get(key);

                nameValuePairs.add(new BasicNameValuePair(""+key, ""+value.replace("%20", " ").replace("%0A", "<br>")));
            }

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            InputStream inputStream = response.getEntity().getContent();
            InputStreamToString str = new InputStreamToString();

            responseServer = str.getStringFromInputStream(inputStream);
            Log.e("RES", responseServer);

            jsonRespuesta = new JSONObject(responseServer);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return jsonRespuesta;
    }

    /*public static class InputStreamToString {
        private static String getStringFromInputStream(InputStream is) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }*/

}
