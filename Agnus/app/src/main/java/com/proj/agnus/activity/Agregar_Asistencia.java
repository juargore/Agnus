package com.proj.agnus.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Asistencias;
import com.proj.agnus.comun.Asistencia_Alumno;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.conexion.connect_HttpPost;
import com.proj.agnus.conexion.connect_HttpsPost;
import com.proj.agnus.fragments.Fragment_Mes_Asistencias;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Agregar_Asistencia extends Activity {

    ActionBar actionBar;
    SharedPreferences prefs;
    String url_logo, id_esc, id_uac, nombre_uac, fecha, fecha_actual, mes, anio;
    int id_spinner;
    TextView txtNombreMateriaAgregar;
    Spinner spinner_sesion_agregar_asis;
    RecyclerView rvListaAgregarAsistencia;
    Button btnGuardarAgregar;
    JSONObject jsonObjRecibido;
    List<String> spinner_lista;
    List<Periodo> opciones_spinner;
    TextView et_fecha_agregar_asist;
    public Adaptador_Asistencias adapter;
    List<Asistencia_Alumno> lista = new ArrayList<>();
    String url_listado = "agnus.mx/app/asistencias.php";
    boolean hayCambios = false;
    boolean cambiosGuardados = false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_asistencia);

        //Personalizar ActionBar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Nueva Asistencia");
        actionBar.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#307095")));

        id_esc = getIntent().getExtras().getString("id_esc");
        fecha_actual = getIntent().getExtras().getString("fecha");
        id_uac = getIntent().getExtras().getString("id_uac");
        nombre_uac = getIntent().getExtras().getString("nombre_uac");
        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        url_logo = prefs.getString("logo", "");

        mes = fecha_actual.substring(fecha_actual.length() - 2);
        anio = fecha_actual.length() < 4 ? fecha_actual : fecha_actual.substring(0, 4);

        lista.clear();
        lista = Fragment_Mes_Asistencias.lista1;

        for (int j = 0; j<lista.size(); j++){
            lista.get(j).setEstado_actual(1);
            lista.get(j).setEstado_nuevo(1);

        }

        txtNombreMateriaAgregar = (TextView) findViewById(R.id.txtNombreMateriaAgregar);
        spinner_sesion_agregar_asis = (Spinner) findViewById(R.id.spinner_sesion_agregar_asis);
        rvListaAgregarAsistencia = (RecyclerView) findViewById(R.id.rvListaAgregarAsistencia);
        btnGuardarAgregar = (Button) findViewById(R.id.btnGuardarAgregar);
        et_fecha_agregar_asist = (TextView) findViewById(R.id.et_fecha_agregar_asist);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvListaAgregarAsistencia.setLayoutManager(layoutManager);

        Calendar calendar = Calendar.getInstance();
        int days = calendar.get(Calendar.DAY_OF_MONTH);
        String day = String.valueOf(days);

        if(day.length() == 1){
            day = "0"+day;
        }

        String formattedDate = anio+"-"+mes+"-"+day;

        et_fecha_agregar_asist.setText(formattedDate);
        txtNombreMateriaAgregar.setText(nombre_uac);

        et_fecha_agregar_asist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                final AlertDialog.Builder Dialog = new AlertDialog.Builder(Agregar_Asistencia.this);
                View layout = inflater.inflate(R.layout.pop_up_asistencia, (ViewGroup) findViewById(R.id.rlDatePicker));

                final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);
                final Button btnDatePicker = (Button) layout.findViewById(R.id.btnDatePicker);

                Calendar calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePicker.updateDate(Integer.parseInt(anio), Integer.parseInt(mes)-1, day);

                Calendar mycal = new GregorianCalendar(Integer.parseInt(anio), Integer.parseInt(mes)-1, 1);
                int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

                calendar.set(Integer.parseInt(anio), Integer.parseInt(mes)-1, daysInMonth);
                datePicker.setMaxDate(calendar.getTimeInMillis());

                calendar.set(Integer.parseInt(anio), Integer.parseInt(mes)-1, 1);
                datePicker.setMinDate(calendar.getTimeInMillis());

                Dialog.setView(layout);
                final AlertDialog alert = Dialog.create();
                alert.show();

                btnDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mes = datePicker.getMonth()+1;
                        int dia = datePicker.getDayOfMonth();

                        String month = mes/10==0?("0"+mes): String.valueOf(mes);
                        String day = dia/10==0?("0"+dia): String.valueOf(dia);
                        fecha = datePicker.getYear()+"-"+month+"-"+day;
                        et_fecha_agregar_asist.setText(fecha);
                        alert.dismiss();
                    }
                });

            }
        });

        TareaAsincrona tareaAsincrona = new TareaAsincrona(1);
        tareaAsincrona.execute();
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

        ImageView icono_escuela = new ImageView(Agregar_Asistencia.this);
        icono_escuela.setPadding(0,30,0,30);
        icono_escuela.setImageDrawable(new BitmapDrawable(getResources(), logo));

        MenuItem item = menu1.getItem(0);
        item.setActionView(icono_escuela);

        return super.onCreateOptionsMenu(menu);
    }

    //Botón regresar del ActionBar
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
                            .setMessage("Hay asistencias sin guardar, ¿Deseas guardar los cambios o descartarlos?")
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
                                    Agregar_Asistencia.this.finish();
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

        HashMap<String, String> parametros_lista_agregar = new HashMap<>();
        parametros_lista_agregar.put("fun","6");
        parametros_lista_agregar.put("esc",""+id_esc);

        jsonObjRecibido = conectar.connect(url_listado, parametros_lista_agregar, Agregar_Asistencia.this);

        spinner_lista = new ArrayList<>();
        opciones_spinner = new ArrayList<>();

        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("select").length(); i++){
                spinner_lista.add(jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("nom"));
                opciones_spinner.add(new Periodo(
                        Integer.parseInt(jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("id")),
                        jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("nom")));
            }

            for(int i = 0; i < lista.size(); i++){
                lista.get(i).setTextom1(jsonObjRecibido.getJSONArray("maquina").getJSONObject(2).getString("texto"));
                lista.get(i).setColorm1(jsonObjRecibido.getJSONArray("maquina").getJSONObject(2).getString("color"));

                lista.get(i).setTexto0(jsonObjRecibido.getJSONArray("maquina").getJSONObject(1).getString("texto"));
                lista.get(i).setColor0(jsonObjRecibido.getJSONArray("maquina").getJSONObject(1).getString("color"));

                lista.get(i).setTexto1(jsonObjRecibido.getJSONArray("maquina").getJSONObject(0).getString("texto"));
                lista.get(i).setColor1(jsonObjRecibido.getJSONArray("maquina").getJSONObject(0).getString("color"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        if(lista.size()>0){
            adapter = new Adaptador_Asistencias(lista, Agregar_Asistencia.this, new Adaptador_Asistencias.OnItemClickListener() {
                @Override
                public void OnItemClick(Asistencia_Alumno asistencia_alumno) {
                    String codigo = asistencia_alumno.getCodigo();
                    int estado_nuevo = asistencia_alumno.getEstado_nuevo();

                    for (int i = 0; i<lista.size(); i++){
                        if(codigo == lista.get(i).getCodigo()){
                            lista.get(i).setEstado_nuevo(estado_nuevo);
                        }
                    }

                    hayCambios = true;
                }
            }, 2, 2);

            rvListaAgregarAsistencia.setAdapter(adapter);

            btnGuardarAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TareaAsincrona tareaAsincrona = new TareaAsincrona(2);
                    tareaAsincrona.execute();

                }
            });
        } else{
            rvListaAgregarAsistencia.setVisibility(View.GONE);
            btnGuardarAgregar.setVisibility(View.GONE);
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Agregar_Asistencia.this, R.layout.spinner_item_izq, spinner_lista);
        spinner_sesion_agregar_asis.setAdapter(dataAdapter);

        spinner_sesion_agregar_asis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_spinner = opciones_spinner.get(i).obtenerId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void enviarDatos(){
        String urlParameters = "";
        connect_HttpsPost conectar = new connect_HttpsPost();

        urlParameters = "fun=7&" +
                "esc="+id_esc+"&" +
                "fecha="+et_fecha_agregar_asist.getText().toString()+"&" +
                "id_uac="+id_uac+"&" +
                "tot_asis="+id_spinner+"&";

        for (int j = 0; j< lista.size(); j++){
            urlParameters = urlParameters + "alumnos["+j+"][]="+lista.get(j).getCodigo()+"&" +
                    "alumnos["+j+"][]="+lista.get(j).getEstado_nuevo()+"&";
        }

        jsonObjRecibido = new JSONObject();
        jsonObjRecibido = conectar.connect(url_listado, urlParameters, Agregar_Asistencia.this);
    }

    public void mostrarMensaje(){
        try {
            if(jsonObjRecibido.has("status") && jsonObjRecibido.getString("status").equals("success")){
                if(jsonObjRecibido.has("mensaje")){
                    Toast.makeText(Agregar_Asistencia.this, "" + jsonObjRecibido.getString("mensaje"), Toast.LENGTH_SHORT).show();
                }

                setResult(10001);
                cambiosGuardados = true;
                Agregar_Asistencia.this.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(Agregar_Asistencia.this);
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
                pDialog.setMessage("Espere un momento por favor\n\nGuardando nueva asistencia...");
            }
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
        protected void onPostExecute(Boolean result) {
            if(result) { pDialog.dismiss(); }
            if(procedencia == 1){
                adaptadorLleno();
            }else {
                mostrarMensaje();
            }
        }
    }
}
