package com.proj.agnus.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Asistencias_Detalle;
import com.proj.agnus.comun.Asistencia_Alumno_Detalle;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;
import com.proj.agnus.conexion.connect_HttpsPost;
import com.proj.agnus.fragments.Fragment_Asistencias;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Asistencia_Detalle extends Activity {

    private int tipo_usuario;
    ActionBar actionBar;
    Button btnGuardarAsisDetalle;
    String url_logo, nombre_alumno_materia, user, id_escuela, fecha, uac;
    SharedPreferences prefs;
    EditText etNombreAlumnoAsis;
    TextView txtNombreMateriaAsis, txtPorcentajeDetalleAsis, txtNoAs;
    String url_listado = "agnus.mx/app/asistencias.php";
    ArrayList<Asistencia_Alumno_Detalle> lista = new ArrayList<>();
    Adaptador_Asistencias_Detalle adapter;
    RecyclerView listaAsistenciaDetalle;
    JSONObject jsonObjRecibido;
    boolean mostrarMensaje = false;
    boolean hayCambios = false;
    boolean cambiosGuardados = false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_detalle);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Asistencia Detalle");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#307095")));

        //Clear focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        url_logo = prefs.getString("logo", "");

        nombre_alumno_materia = getIntent().getExtras().getString("nombre_alumno_materia");
        user = getIntent().getExtras().getString("user");
        id_escuela = getIntent().getExtras().getString("id_escuela");
        fecha = getIntent().getExtras().getString("fecha");
        uac = getIntent().getExtras().getString("uac");

        txtNoAs = (TextView) findViewById(R.id.txtNoAs);
        etNombreAlumnoAsis = (EditText) findViewById(R.id.etNombreAlumnoAsis);
        txtNombreMateriaAsis = (TextView) findViewById(R.id.txtNombreMateriaAsis);
        txtPorcentajeDetalleAsis = (TextView) findViewById(R.id.txtPorcentajeDetalleAsis);
        listaAsistenciaDetalle = (RecyclerView) findViewById(R.id.listaAsistenciaDetalle);
        btnGuardarAsisDetalle = (Button) findViewById(R.id.btnGuardarAsisDetalle);

        //Agregar datos informativos
        if(tipo_usuario == 2){ //Profesor
            txtNombreMateriaAsis.setText(Fragment_Asistencias.nombre_uac);
            etNombreAlumnoAsis.setText(nombre_alumno_materia);
            //btnGuardarAsisDetalle.setVisibility(View.VISIBLE);
        } else { //Padre o alumno
            txtNombreMateriaAsis.setText(nombre_alumno_materia);
            etNombreAlumnoAsis.setText(Fragment_Asistencias.nombre_uac);
            //btnGuardarAsisDetalle.setVisibility(View.GONE);
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaAsistenciaDetalle.setLayoutManager(layoutManager);

        check_internet_connection check = new check_internet_connection();

        if(check.isConnected(Asistencia_Detalle.this)){
            TareaAsincrona tarea = new TareaAsincrona(1);
            tarea.execute();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Asistencia_Detalle.this);
            builder1.setMessage("No se logró conectar al servidor. Verifique su conexión e intente de nuevo");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
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

        ImageView icono_escuela = new ImageView(Asistencia_Detalle.this);
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
            if(hayCambios){
                if(cambiosGuardados == true){
                    finish();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Cambios detectados!")
                            .setMessage("Hay cambios sin guardar. ¿Deseas guardar los cambios o descartarlos?")
                            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    TareaAsincrona tarea = new TareaAsincrona(2);
                                    tarea.execute();
                                }
                            })
                            .setNegativeButton("Descartar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Asistencia_Detalle.this.finish();
                                }
                            })
                            .show();
                }
            } else{
                finish();
            }
        }
        return true;
    }

    public void llenarLista(){
        connect_HttpPost conectar = new connect_HttpPost();

        HashMap<String, String> parametros_eventos_calendario = new HashMap<>();
        parametros_eventos_calendario.put("fun","5");
        parametros_eventos_calendario.put("esc",""+id_escuela);
        parametros_eventos_calendario.put("user",""+user);
        parametros_eventos_calendario.put("fecha",""+fecha);
        parametros_eventos_calendario.put("uac",""+uac);

        jsonObjRecibido = conectar.connect(url_listado, parametros_eventos_calendario, Asistencia_Detalle.this);

        try {
            if(jsonObjRecibido.getString("status").equals("fail") || jsonObjRecibido.has("mensaje")){
                mostrarMensaje = true;
            } else {
                mostrarMensaje = false;

                for(int i = 0; i < jsonObjRecibido.getJSONArray("listado").length(); i++){
                    String colorm1, color0, color1, textm1, text0, text1;
                    int id_asis, cant_asis, estado_actual, tipo;

                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).has("color-1")){
                        colorm1 = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).getString("color-1");
                    } else {
                        colorm1 = "";
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).has("color0")){
                        color0 = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).getString("color0");
                    } else {
                        color0 = "";
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).has("color1")){
                        color1 = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).getString("color1");
                    } else {
                        color1 = "";
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).has("text-1")){
                        textm1 = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).getString("text-1");
                    } else {
                        textm1 = "";
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).has("text0")){
                        text0 = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).getString("text0");
                    } else {
                        text0 = "";
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).has("text1")){
                        text1 = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getJSONArray("maquina").getJSONObject(0).getString("text1");
                    } else {
                        text1 = "";
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getString("id_asis").equals("")){
                        id_asis = 0;
                    } else {
                        id_asis = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getInt("id_asis");
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getString("cant_asis").equals("")){
                        cant_asis = 0;
                    } else {
                        cant_asis = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getInt("cant_asis");
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getString("estadoactual").equals("")){
                        estado_actual = 0;
                    } else {
                        estado_actual = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getInt("estadoactual");
                    }
                    if(jsonObjRecibido.getJSONArray("listado").getJSONObject(i).has("tipo")){
                        tipo = jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getInt("tipo");
                    } else {
                        tipo = 0;
                    }

                    lista.add(new Asistencia_Alumno_Detalle(i,
                            jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getString("etiqueta"),
                            jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getString("color"),
                            jsonObjRecibido.getJSONArray("listado").getJSONObject(i).getString("fecha"),
                            colorm1,
                            color0,
                            color1,
                            textm1,
                            text0,
                            text1,
                            id_asis,
                            cant_asis,
                            estado_actual,
                            estado_actual,
                            tipo
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        adapter = new Adaptador_Asistencias_Detalle(tipo_usuario, lista, Asistencia_Detalle.this, new Adaptador_Asistencias_Detalle.OnItemClickListener() {
            @Override
            public void OnItemClick(Asistencia_Alumno_Detalle asistencia_alumno_detalle) {
                for (int i = 0; i<lista.size(); i++){
                    if(asistencia_alumno_detalle.getId_asis() == lista.get(i).getId_asis()){
                        lista.get(i).setEstado_nuevo(asistencia_alumno_detalle.getEstado_nuevo());
                    }
                }
                hayCambios = true;
                btnGuardarAsisDetalle.setVisibility(View.VISIBLE);
            }
        });

        listaAsistenciaDetalle.setAdapter(adapter);

        btnGuardarAsisDetalle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TareaAsincrona tarea = new TareaAsincrona(2);
                tarea.execute();
            }
        });
    }

    public void enviarDatos(){
        String urlParameters = "";
        connect_HttpsPost conectar = new connect_HttpsPost();

        urlParameters = "fun=8&" +
                "esc="+id_escuela+"&";

        for (int j = 0; j< lista.size(); j++){
            if(lista.get(j).getTipo() == 1){
                urlParameters = urlParameters + "ids_asis["+j+"][]="+lista.get(j).getId_asis()+"&" +
                        "ids_asis["+j+"][]="+lista.get(j).getEstado_nuevo()+"&" +
                        "ids_asis["+j+"][]="+lista.get(j).getCant_asis()+"&";
            }
        }

        jsonObjRecibido = new JSONObject();
        jsonObjRecibido = conectar.connect(url_listado, urlParameters, Asistencia_Detalle.this);
    }

    public void mostrarMensaje(){
        try {
            if(jsonObjRecibido.has("status") || jsonObjRecibido.get("status").equals("success")){
                if(jsonObjRecibido.has("mensaje")){
                    Toast.makeText(Asistencia_Detalle.this, "" + jsonObjRecibido.getString("mensaje"), Toast.LENGTH_SHORT).show();
                }
                cambiosGuardados = true;
                setResult(10001);
                Asistencia_Detalle.this.finish();
            } else {
                if(jsonObjRecibido.has("mensaje")){
                    Toast.makeText(Asistencia_Detalle.this, "" + jsonObjRecibido.getString("mensaje"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(Asistencia_Detalle.this);
        int procedencia;

        public TareaAsincrona(int procedencia){
            this.procedencia = procedencia;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if(procedencia == 1){
                pDialog.setMessage("Espere un momento por favor\n\nDescargando contenido...");
            } else{
                pDialog.setMessage("Espere un momento por favor\n\nGuardando cambios de asistencias...");
            }
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(procedencia == 1){
                llenarLista();
            } else{
                enviarDatos();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(procedencia == 1){
                adaptadorLleno();
                try {
                    txtPorcentajeDetalleAsis.setText(jsonObjRecibido.getJSONObject("recuadro").getString("texto"));
                    txtPorcentajeDetalleAsis.setBackgroundColor(Color.parseColor(jsonObjRecibido.getJSONObject("recuadro").getString("color")));
                    if(mostrarMensaje){
                        listaAsistenciaDetalle.setVisibility(View.GONE);
                        txtNoAs.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                mostrarMensaje();
            }

            pDialog.dismiss();

        }
    }

}
