package com.proj.agnus.seguridad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.comun.Escuelas;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recuperar_Contrasena extends Activity implements AdapterView.OnItemSelectedListener {

    private TextView txtCancelarRC;
    private EditText etEmailRC;
    private Button btnRecuperarContrasena;
    private JSONObject jsonObjRecibido;
    private Spinner spinner_escuelas_RC;
    private ArrayList<Escuelas> escuelas;
    public connect_HttpPost conectar;
    private int id_escuela = 0;
    ProgressDialog pDialog;
    List<String> escuelas_lista;
    HashMap<String, String> parametros_escuelas;
    TareaAsincrona tarea;
    private String url_pantalla_login = "agnus.mx/app/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        txtCancelarRC = (TextView) findViewById(R.id.txtCancelarRC);
        etEmailRC = (EditText) findViewById(R.id.etEmailRC);
        //spinner_escuelas_RC = (Spinner) findViewById(R.id.spinner_escuelas_RC);
        btnRecuperarContrasena = (Button) findViewById(R.id.btnRecuperarContrasena);

        spinner_escuelas_RC.setOnItemSelectedListener(this);
        escuelas_lista = new ArrayList<>();
        conectar = new connect_HttpPost();
        escuelas = new ArrayList<>();

        //Definir los parametros que se envian al WebService
        parametros_escuelas = new HashMap<>();
        parametros_escuelas.put("fun","7");

        //La clase connect_HttpPost siempre regresa un objeto JSON {}
        jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_escuelas, Recuperar_Contrasena.this);

        //Se guarda la informacion en el objeto "Escuelas" que extiende a "Elemento"
        escuelas_lista.add("Selecciona una escuela");
        escuelas.add(new Escuelas(-1,"","0"));
        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("escuelas").length(); i++){
                escuelas_lista.add(jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getString("nombre"));
                escuelas.add(new Escuelas(
                        Integer.parseInt(jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getString("id")),
                        jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getString("nombre"),
                        "0"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Propiedades y asignacion al Spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, escuelas_lista);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_escuelas_RC.setAdapter(dataAdapter);
        spinner_escuelas_RC.setSelection(0, true);

        btnRecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposLlenos()){
                    if(id_escuela != -1){
                        tarea = new TareaAsincrona();
                        tarea.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Seleccione una escuela del listado", Toast.LENGTH_LONG).show();
                    }

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
        if(TextUtils.isEmpty(etEmailRC.getText().toString())){
            etEmailRC.setError("Este campo no puede estar vacio");
            return false;
        } else {
            return true;
        }
    }

    public void enviar_datos_recuperacion(){
        HashMap<String, String> parametros_autenticacion = new HashMap<>();
        parametros_autenticacion.put("fun","6");
        parametros_autenticacion.put("esc", ""+id_escuela);
        parametros_autenticacion.put("correo",etEmailRC.getText().toString().replace(" ",""));
        jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_autenticacion, Recuperar_Contrasena.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#0D3863"));
        ((TextView) adapterView.getChildAt(0)).setTextSize(12);
        id_escuela = escuelas.get(i).obtenerId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Recuperar_Contrasena.this);
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

                new AlertDialog.Builder(Recuperar_Contrasena.this)
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
