package com.proj.agnus.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.comun.Person;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Email extends Activity implements AdapterView.OnItemSelectedListener {

    ArrayList<Person> people;
    ArrayAdapter<Person> adapter;
    ArrayList<Periodo> opciones_spinner;
    List<String> opciones_lista;
    ArrayList<String> IDs_respuesta;
    ContactsCompletionView completionView;
    ImageView ivSendEmail, ivDownArrow;
    EditText etAsuntoEmail, etMensajeEmail;
    Spinner spinner_tipo_email;
    JSONObject jsonObjRecibido;
    connect_HttpPost conectar;
    String tipo_usuario, id_esc, id_select, id_correo,
            nombre_activity, id_usuario, asunto, url_logo,
            url_pantalla_email = "agnus.mx/app/correo.php";
    SharedPreferences prefs;
    ActionBar actionBar;
    private int tipo;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Redactar");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#307095")));

        spinner_tipo_email = (Spinner) findViewById(R.id.spinner_tipo_email);
        completionView = (ContactsCompletionView)findViewById(R.id.searchView);
        etAsuntoEmail = (EditText) findViewById(R.id.etAsuntoEmail);
        etMensajeEmail = (EditText) findViewById(R.id.etMensajeEmail);
        ivDownArrow = (ImageView) findViewById(R.id.ivDownArrow);
        ivSendEmail = (ImageView) findViewById(R.id.ivSendEmail);
        spinner_tipo_email.setOnItemSelectedListener(this);

        completionView.allowDuplicates(false);
        nombre_activity = getIntent().getExtras().getString("activity_name");

        if(nombre_activity.equals("Respuesta_Email")){
            spinner_tipo_email.setEnabled(false);
            ivDownArrow.setVisibility(View.GONE);

            asunto = getIntent().getExtras().getString("asunto");
            IDs_respuesta = getIntent().getStringArrayListExtra("lista_destinatarios");
            id_correo = getIntent().getExtras().getString("id_correo");

            tipo = Integer.parseInt(getIntent().getExtras().getString("select"));
            etAsuntoEmail.setText("RE: "+asunto);
        }

        opciones_lista = new ArrayList<>();
        conectar = new connect_HttpPost();
        opciones_spinner = new ArrayList<>();
        people = new ArrayList<>();

        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = prefs.getString("tipo_usuario", "1");
        id_esc = prefs.getString("id_escuela", "");
        id_usuario = prefs.getString("codigo_usuario", "");
        url_logo = prefs.getString("logo", "");


        HashMap<String, String> parametros_email = new HashMap<>();
        parametros_email.put("fun","6");
        parametros_email.put("tipo",""+tipo_usuario);
        jsonObjRecibido = conectar.connect(url_pantalla_email, parametros_email, Email.this);

        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("select").length(); i++){
                opciones_lista.add(jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("opcion"));
                opciones_spinner.add(new Periodo(
                        Integer.parseInt(jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("id")),
                        jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("opcion")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Email.this, R.layout.spinner_item_izq, opciones_lista);
        spinner_tipo_email.setAdapter(dataAdapter);

        if(nombre_activity.equals("Respuesta_Email")){
            for(int i = 0; i<opciones_spinner.size(); i++){
                if(opciones_spinner.get(i).obtenerId() == tipo){
                    spinner_tipo_email.setSelection(i, true);
                    break;
                }
            }
        } else { spinner_tipo_email.setSelection(0, true); }

        //Send Email
        ivSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(etAsuntoEmail.getText().toString())){
                    etAsuntoEmail.setError("Agregue un Asunto a este Email");
                } else if(TextUtils.isEmpty(etMensajeEmail.getText().toString())) {
                    etMensajeEmail.setError("Escriba un mensaje para enviarlo");
                } else if(TextUtils.isEmpty(completionView.getText().toString())){
                    completionView.setError("Debe haber al menos un destinatario");
                } else{
                    ArrayList<Person> destinatarios = (ArrayList<Person>) completionView.getObjects();

                    HashMap<String, String> parametros_email = new HashMap<>();
                    parametros_email.put("fun","8");
                    parametros_email.put("esc",""+id_esc);

                    for (int i = 0; i< destinatarios.size(); i++){
                        parametros_email.put("para["+i+"]",""+destinatarios.get(i).getId());
                    }

                    String mensaje = String.valueOf(etMensajeEmail.getText());
                    mensaje = mensaje.replace(" ","%20").replace("\n","%0A");
                    String asunto = etAsuntoEmail.getText().toString().replace(" ", "%20");

                    parametros_email.put("asunto",""+asunto);
                    parametros_email.put("mensaje",""+mensaje);
                    parametros_email.put("select",""+id_select);
                    parametros_email.put("tipo_ses",""+tipo_usuario);
                    parametros_email.put("user_ses",""+id_usuario);

                    if(nombre_activity.equals("Respuesta_Email")){
                        parametros_email.put("res",""+id_correo);
                    } else {
                        parametros_email.put("res","0");
                    }
                    parametros_email.put("adjuntos","0");

                    TareaAsincrona tarea = new TareaAsincrona(parametros_email, 2, getApplicationContext());
                    tarea.execute();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#0D3863"));
        ((TextView) adapterView.getChildAt(0)).setTextSize(12);

        id_select = String.valueOf(opciones_spinner.get(i).obtenerId());
        completionView.clearListSelection();
        completionView.clear();

        HashMap<String, String> parametros_email = new HashMap<>();
        parametros_email.put("fun","7");
        parametros_email.put("esc",""+id_esc);
        parametros_email.put("tipo",""+tipo_usuario);
        parametros_email.put("user",""+id_usuario);

        if(nombre_activity.equals("Respuesta_Email")){
            parametros_email.put("select",""+tipo);
            parametros_email.put("res",""+1);
            for (int j = 0; j< IDs_respuesta.size(); j++){
                parametros_email.put("usu["+j+"]",""+IDs_respuesta.get(j));
            }
        } else {
            parametros_email.put("select",""+id_select);
            parametros_email.put("res",""+0);
            parametros_email.put("usu",""+0);
        }

        TareaAsincrona tarea = new TareaAsincrona(parametros_email, 1, getApplicationContext());
        tarea.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bitmap logo = null;
        Menu menu1 = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        Descargar_Imagenes di = new Descargar_Imagenes();
        try {
            logo = di.obtenerImagen(url_logo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        ImageView icono_escuela = new ImageView(Email.this);
        icono_escuela.setPadding(0,30,0,30);
        icono_escuela.setImageDrawable(new BitmapDrawable(getResources(), logo));

        MenuItem item = menu1.getItem(0);
        item.setActionView(icono_escuela);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }


    public void llenarListaRemitentes(JSONObject jsonObjRecibido){
        people.clear();
        completionView.clear();

        try {
            //Add to Person Object
            for(int i = 0; i < jsonObjRecibido.getJSONArray("usuarios").length(); i++){
                people.add(new Person(
                        jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("opcion"),
                        jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("id")));
            }

            //Get "sel":"1" to show on ContactsCompletionView
            if(nombre_activity.equals("Respuesta_Email")){
                for(int i = 0; i < jsonObjRecibido.getJSONArray("usuarios").length(); i++){
                    if(Integer.parseInt(jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("sel")) == 1){
                        completionView.addObject(new Person(jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("opcion"),
                                jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("id")));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        String mensaje_respuesta, status_respuesta;
        ProgressDialog pDialog = new ProgressDialog(Email.this);
        HashMap<String, String> parametros_email;
        private int funcion;
        Context ctx;

        public TareaAsincrona(HashMap<String, String> parametros_email, int funcion, Context ctx){
            this.parametros_email = parametros_email;
            this.funcion = funcion;
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if(funcion == 1){ pDialog.setMessage("Actualizando lista...");
            } else { pDialog.setMessage("Enviando mensaje..."); }
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            conectar = new connect_HttpPost();
            jsonObjRecibido = conectar.connect(url_pantalla_email, parametros_email, Email.this);

            if(funcion == 1){
                //Update content ContactsCompletionView
                llenarListaRemitentes(jsonObjRecibido);
            } else {
                try {
                    status_respuesta = jsonObjRecibido.getString("status");
                    mensaje_respuesta = jsonObjRecibido.getString("mensaje");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) { pDialog.dismiss(); }

            if(funcion == 1){
                //Update content ContactsCompletionView
                adapter = new ArrayAdapter<>(Email.this, R.layout.spinner_item_izq, people);
                completionView.setAdapter(adapter);

            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder(Email.this);
                builder.setTitle(" Agnus");
                builder.setMessage("\n"+mensaje_respuesta);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setCancelable(false);
                final AlertDialog alert1 = builder.create();
                alert1.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(status_respuesta.equals("success")){
                            alert1.dismiss();
                            finish();
                        } else {
                            alert1.dismiss();
                        }
                    }
                }, 2000);

            }

        }
    }
}
