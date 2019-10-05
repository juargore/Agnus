package com.proj.agnus.seguridad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Recuperar_Contrasena1 extends Activity {

    private TextView txtCancelarRC;
    private EditText etEmailRC, etCodigoRC;
    private Button btnRecuperarContrasena;
    private JSONObject jsonObjRecibido;
    public connect_HttpPost conectar;
    ProgressDialog pDialog;
    private String url_pantalla_login = "agnus.mx/app/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        txtCancelarRC = (TextView) findViewById(R.id.txtCancelarRC);
        etEmailRC = (EditText) findViewById(R.id.etEmailRC);
        etCodigoRC = (EditText) findViewById(R.id.etCodigoRC);
        btnRecuperarContrasena = (Button) findViewById(R.id.btnRecuperarContrasena);

        conectar = new connect_HttpPost();
        btnRecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposLlenos()){
                    TareaAsincrona tarea = new TareaAsincrona();
                    tarea.execute();
                }
            }
        });

        txtCancelarRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public boolean validarCamposLlenos(){
        if(TextUtils.isEmpty(etCodigoRC.getText().toString())){
            etCodigoRC.setError("Este campo no puede estar vacio");
            return false;
        } else if (TextUtils.isEmpty(etEmailRC.getText().toString())){
            etEmailRC.setError("Este campo no puede estar vacio");
            return false;
        }else {
            return true;
        }
    }

    public void enviar_datos_recuperacion(){
        HashMap<String, String> parametros_autenticacion = new HashMap<>();
        parametros_autenticacion.put("fun","6");
        parametros_autenticacion.put("codigo",etCodigoRC.getText().toString().replace(" ",""));
        parametros_autenticacion.put("correo",etEmailRC.getText().toString().replace(" ",""));

        jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_autenticacion, Recuperar_Contrasena1.this);
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Recuperar_Contrasena1.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Enviando datos al Servidor...");
            pDialog.setCancelable(true);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            enviar_datos_recuperacion();
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pDialog.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) { pDialog.dismiss(); }
            try {
                String msj = jsonObjRecibido.getString("mensaje");
                final String status = jsonObjRecibido.getString("status");

                new AlertDialog.Builder(Recuperar_Contrasena1.this)
                        .setMessage(""+msj)
                        .setNeutralButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(status.equals("success")){
                                    finish();
                                }
                            }
                        })
                .show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
