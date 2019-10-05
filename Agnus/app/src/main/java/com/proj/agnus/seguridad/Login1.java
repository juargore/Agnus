package com.proj.agnus.seguridad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.activity.MainActivity;
import com.proj.agnus.comun.Escuelas;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login1 extends Activity implements View.OnClickListener {

    private ArrayList<Escuelas> escuelas;
    private TextView txtRecuperarContrasena, txtPoliticas;
    private EditText etCodigo, etContrasena;
    private Button btnIngresar;
    private CheckBox chBx_login, chBx_politicas;
    private String url_pantalla_login = "agnus.mx/app/login.php";
    private String url_pantalla_token = "agnus.mx/app/token.php";
    private JSONObject jsonObjRecibido;
    private int id_escuela = -1;
    private String nombre_escuela, tipo_usuario;
    public connect_HttpPost conectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCodigo = (EditText) findViewById(R.id.etCodigo);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        chBx_login = (CheckBox) findViewById(R.id.chBx_login);
        chBx_politicas = (CheckBox) findViewById(R.id.chBx_politicas);
        etContrasena = (EditText) findViewById(R.id.etContrasena);
        txtRecuperarContrasena = (TextView) findViewById(R.id.txtRecuperarContrasena);
        txtPoliticas = (TextView) findViewById(R.id.txtPoliticas);

        txtPoliticas.setOnClickListener(this);
        txtRecuperarContrasena.setOnClickListener(this);
        btnIngresar.setOnClickListener(this);
        conectar = new connect_HttpPost();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtRecuperarContrasena:
                Intent abrir_recuperar = new Intent(Login1.this, Recuperar_Contrasena1.class);
                startActivity(abrir_recuperar);
                break;

            case R.id.btnIngresar:
                if (validarCamposLlenos()){
                    if(chBx_politicas.isChecked()){
                        TareaAsincrona tarea = new TareaAsincrona(1);
                        tarea.execute();
                    } else {
                        Toast.makeText(this, "Para continuar debes aceptar las Polítcas de Privacidad", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.txtPoliticas:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://agnus.mx/landing/aviso.php"));
                startActivity(browserIntent);
                break;
        }
    }

    public void enviar_credenciales(int procedencia){

        if(procedencia == 1){
            HashMap<String, String> parametros_autenticacion = new HashMap<>();
            parametros_autenticacion.put("fun","1");
            parametros_autenticacion.put("user", etCodigo.getText().toString().replace(" ",""));
            parametros_autenticacion.put("pass",etContrasena.getText().toString().replace(" ",""));

            jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_autenticacion, Login1.this);
        } else if(procedencia == 2){
            HashMap<String, String> parametros_autenticacion = new HashMap<>();
            parametros_autenticacion.put("fun","2");
            parametros_autenticacion.put("user", etCodigo.getText().toString().replace(" ",""));
            parametros_autenticacion.put("tipo", tipo_usuario);
            parametros_autenticacion.put("esc", ""+id_escuela);

            jsonObjRecibido = new JSONObject();
            jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_autenticacion, Login1.this);
        }

    }

    public void funcion_dos(){
        try {
            if(jsonObjRecibido.getString("status").equalsIgnoreCase("success")){

                //Checar que el array de escuelas sea igual a 1
                if(jsonObjRecibido.getJSONArray("escuelas").length() > 1){ //Hay mas de una escuela

                    final List<String> escuelas_lista = new ArrayList<>();
                    escuelas = new ArrayList<>();

                    //Se guarda la informacion en el objeto "Escuelas" que extiende a "Elemento"
                    escuelas_lista.add("Selecciona una escuela");
                    escuelas.add(new Escuelas(-1,"","0"));
                    try {
                        for(int i = 0; i < jsonObjRecibido.getJSONArray("escuelas").length(); i++){
                            escuelas_lista.add(jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getString("nombre"));
                            escuelas.add(new Escuelas(
                                    jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getInt("esc"),
                                    jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getString("nombre"),
                                    jsonObjRecibido.getJSONArray("escuelas").getJSONObject(i).getString("tipo_usuario")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                    final AlertDialog.Builder Dialog = new AlertDialog.Builder(Login1.this);
                    View layout = inflater.inflate(R.layout.pop_up_login, (ViewGroup) findViewById(R.id.layPopUpLogin));

                    //Propiedades y asignacion al Spinner
                    final Spinner spinnerPopUpLogin = (Spinner)layout.findViewById(R.id.spinnerPopUpLogin);
                    final Button btnPopUpLogin = (Button) layout.findViewById(R.id.btnPopUpLogin);

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, escuelas_lista);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPopUpLogin.setAdapter(dataAdapter);
                    spinnerPopUpLogin.setSelection(0, true);

                    spinnerPopUpLogin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#0D3863"));
                            ((TextView) adapterView.getChildAt(0)).setTextSize(12);
                            id_escuela = escuelas.get(i).obtenerId();
                            nombre_escuela = escuelas.get(i).obtenerNombre();
                            tipo_usuario = escuelas.get(i).getTipo_usuario();
                            nombre_escuela = escuelas.get(i).obtenerNombre();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                    Dialog.setView(layout);
                    final AlertDialog alert = Dialog.create();
                    alert.show();

                    btnPopUpLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("Nombre esc",""+nombre_escuela);
                            if(id_escuela == -1){
                                Toast.makeText(Login1.this, "Seleccione una escuela del listado", Toast.LENGTH_SHORT).show();
                            } else{
                                alert.dismiss();
                                //Manda a llamar la funcion 2...
                                TareaAsincrona tarea = new TareaAsincrona(2);
                                tarea.execute();
                            }
                        }
                    });

                } else {
                    //Pasa directo a la funcion 2, solo hay 1 escuela
                    tipo_usuario = jsonObjRecibido.getJSONArray("escuelas").getJSONObject(0).getString("tipo_usuario");
                    id_escuela = jsonObjRecibido.getJSONArray("escuelas").getJSONObject(0).getInt("esc");
                    nombre_escuela = jsonObjRecibido.getJSONArray("escuelas").getJSONObject(0).getString("nombre");

                    TareaAsincrona tarea = new TareaAsincrona(2);
                    tarea.execute();
                }

            } else {
                String msj = jsonObjRecibido.getString("mensaje");
                Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void entrar_app(){
        try {
            if(jsonObjRecibido.getString("status").equals("success")){
                //Se guardan los datos del usuario en SharedPreferences para su uso posterior
                SharedPreferences.Editor editor = getSharedPreferences("Agnus_BD", MODE_PRIVATE).edit();
                editor.putString("codigo_usuario", etCodigo.getText().toString().replace(" ",""));
                editor.putString("contrasena_usuario", etContrasena.getText().toString().replace(" ",""));
                editor.putString("id_escuela", ""+id_escuela);
                editor.putString("nombre_escuela", ""+nombre_escuela);
                editor.putString("tipo_usuario", ""+tipo_usuario);

                if(jsonObjRecibido.has("foto")){editor.putString("foto", jsonObjRecibido.getString("foto"));}
                if(jsonObjRecibido.has("correo")){editor.putString("correo", jsonObjRecibido.getString("correo"));}
                if(jsonObjRecibido.has("genero")){editor.putString("genero", jsonObjRecibido.getString("genero"));}
                if(jsonObjRecibido.has("nombre")){editor.putString("nombre", jsonObjRecibido.getString("nombre"));}
                if(jsonObjRecibido.has("logo")){editor.putString("logo", jsonObjRecibido.getString("logo"));}



                if(chBx_login.isChecked()){
                    editor.putBoolean("recordar_contrasena", true);
                }
                editor.commit();

                //Se envia en numero de notificaicones para mostrar en el menu lateral
                Intent abrir_inicio = new Intent(Login1.this, MainActivity.class);
                abrir_inicio.putExtra("activity_name","Login");
                abrir_inicio.putExtra("area","0");
                if(jsonObjRecibido.has("noticias")){abrir_inicio.putExtra("noticias",""+jsonObjRecibido.getString("noticias"));}
                if(jsonObjRecibido.has("notificaciones")){abrir_inicio.putExtra("notificaciones",""+jsonObjRecibido.getString("notificaciones"));}
                if(jsonObjRecibido.has("calendario")){abrir_inicio.putExtra("calendario",""+jsonObjRecibido.getString("calendario"));}
                if(jsonObjRecibido.has("agenda")){abrir_inicio.putExtra("agenda",""+jsonObjRecibido.getString("agenda"));}
                if(jsonObjRecibido.has("evaluacion")){abrir_inicio.putExtra("evaluacion",""+jsonObjRecibido.getString("evaluacion"));}
                if(jsonObjRecibido.has("email")){abrir_inicio.putExtra("email",""+jsonObjRecibido.getString("email"));}
                if(jsonObjRecibido.has("asistencias")){ abrir_inicio.putExtra("asistencias",""+jsonObjRecibido.getString("asistencias")); }

                if(jsonObjRecibido.has("modulos")){

                    /*JSONArray jsonArray = new JSONArray();
                    jsonArray.put("agenda");
                    jsonArray.put("notificaciones");
                    jsonArray.put("evaluacion");

                    abrir_inicio.putExtra("modulos",""+jsonArray);*/

                    abrir_inicio.putExtra("modulos",""+jsonObjRecibido.getJSONArray("modulos").toString());
                }

                startActivity(abrir_inicio);

                //Se registra el Token
                registrar_token();
                this.finish();
            }else {
                String mensaje = jsonObjRecibido.getString("mensaje");
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registrar_token(){
        SharedPreferences prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        HashMap<String, String> parametros_token = new HashMap<>();
        parametros_token.put("fun","1");
        parametros_token.put("esc",""+id_escuela);
        parametros_token.put("tipo",""+tipo_usuario);
        parametros_token.put("codigo",""+etCodigo.getText().toString().replace(" ",""));
        parametros_token.put("token",""+prefs.getString("token","0"));
        parametros_token.put("plataforma","1");
        jsonObjRecibido = conectar.connect(url_pantalla_token, parametros_token, Login1.this);
    }

    public boolean validarCamposLlenos(){
        if(TextUtils.isEmpty(etCodigo.getText().toString())){
            etCodigo.setError("Este campo no puede estar vacio");
            return false;
        } else if(TextUtils.isEmpty(etContrasena.getText().toString())){
            etContrasena.setError("Este campo no puede estar vacio");
            return false;
        } else {
            return true;
        }
    }


    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        int procedencia;
        ProgressDialog pDialog = new ProgressDialog(Login1.this);

        public TareaAsincrona(int procedencia){
            this.procedencia = procedencia;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Espere un momento por favor\n\nValidando credenciales...");
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            enviar_credenciales(procedencia);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(procedencia == 1){
                funcion_dos();
            } else if (procedencia == 2){
                entrar_app();
            }

            if(result) { pDialog.dismiss(); }

        }
    }
}
