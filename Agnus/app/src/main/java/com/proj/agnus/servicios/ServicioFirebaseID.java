package com.proj.agnus.servicios;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.proj.agnus.conexion.connect_HttpPost;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Arturo on 5/3/18.
 */

public class ServicioFirebaseID extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("--", "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token_nuevo) {
        //Guardar token en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        String token_guardado = prefs.getString("token","0");

        if(token_guardado.equals("0") || token_guardado.equals(null)){ //No se ha registrado token aun
            SharedPreferences.Editor editor = getSharedPreferences("Agnus_BD", MODE_PRIVATE).edit();
            editor.putString("token", ""+token_nuevo);
            editor.commit();
        } else { //Ya hay un token registrado, hay que actualizarlo
            if(!token_guardado.equals(token_nuevo)){ //Si son diferentes, entonces es un nuevo token. Registrarlo!!
                //Actualizar token en Server
                actualizar_token(token_guardado, token_nuevo);
            }
        }
    }

    public void actualizar_token(String token_guardado, String token_nuevo){
        String url_actualizar_token = "agnus.mx/app/agenda.php";
        HashMap<String, String> parametros_autenticacion = new HashMap<>();

        SharedPreferences prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        String id_escuela = prefs.getString("id_escuela","0");

        parametros_autenticacion.put("fun","2");
        parametros_autenticacion.put("esc",""+id_escuela);
        parametros_autenticacion.put("token_anterior", token_guardado);
        parametros_autenticacion.put("token_nuevo", token_nuevo);

        connect_HttpPost conectar = new connect_HttpPost();
        Activity activity = (Activity) getApplicationContext();
        JSONObject jsonObject = conectar.connect(url_actualizar_token, parametros_autenticacion, activity);

        try {
            if(jsonObject.getString("status").equals("success")){
                SharedPreferences.Editor editor = getSharedPreferences("Agnus_BD", MODE_PRIVATE).edit();
                editor.putString("token", ""+token_nuevo);
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
