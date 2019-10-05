package com.proj.agnus.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Email;
import com.proj.agnus.comun.Email;
import com.proj.agnus.comun.Mes;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Email extends Fragment {

    FloatingActionButton fbEmail;
    public Adaptador_Email adapter;
    private ArrayList<Periodo> opciones_spinner;
    private ArrayList<Mes> opciones_spinner2;
    ArrayList<Email> lista = new ArrayList<>();
    SharedPreferences prefs;
    int id_escuela;
    int id_usuario;
    String id_usuario2;
    Spinner spinner_bandejas, spinner_filtro;
    RecyclerView rvListaBandeja;
    public connect_HttpPost conectar;
    private JSONObject jsonObjRecibido;
    int id_baneja = 1;
    TextView txtNoCorreos;
    boolean mostrarTexto = false;
    boolean primeraVez = true;
    List<String> spinner1_lista;
    List<String> spinner2_lista;
    final String url_email = "agnus.mx/app/correo.php";
    SwipeRefreshLayout swipeRefrescarEmail;
    check_internet_connection check;

    public Fragment_Email(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_email, container, false);

        fbEmail = (FloatingActionButton) rootView.findViewById(R.id.fbEmail);
        spinner_bandejas = (Spinner) rootView.findViewById(R.id.spinner_bandejas);
        spinner_filtro = (Spinner) rootView.findViewById(R.id.spinner_filtro);
        rvListaBandeja = (RecyclerView) rootView.findViewById(R.id.rvListaBandeja);
        txtNoCorreos = (TextView) rootView.findViewById(R.id.txtNoCorreos);
        swipeRefrescarEmail = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefrescarEmail);

        conectar = new connect_HttpPost();
        spinner1_lista = new ArrayList<>();
        spinner2_lista = new ArrayList<>();
        opciones_spinner = new ArrayList<>();
        opciones_spinner2 = new ArrayList<>();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        id_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        rvListaBandeja.setLayoutManager(layoutManager);

        HashMap<String, String> parametros_email = new HashMap<>();
        parametros_email.put("fun","1");
        parametros_email.put("user",""+id_usuario);
        parametros_email.put("esc",""+id_escuela);

        check = new check_internet_connection();
        jsonObjRecibido = conectar.connect(url_email, parametros_email, getActivity());

        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("select1").length(); i++){
                spinner1_lista.add(jsonObjRecibido.getJSONArray("select1").getJSONObject(i).getString("opcion"));
                opciones_spinner.add(new Periodo(
                        Integer.parseInt(jsonObjRecibido.getJSONArray("select1").getJSONObject(i).getString("id")),
                        jsonObjRecibido.getJSONArray("select1").getJSONObject(i).getString("opcion")));
            }

            if(jsonObjRecibido.getJSONArray("select2").length() == 0){
                spinner2_lista.add("Sin filtros");
            } else {
                for(int i = 0; i < jsonObjRecibido.getJSONArray("select2").length(); i++){
                    spinner2_lista.add(jsonObjRecibido.getJSONArray("select2").getJSONObject(i).getString("Usuario"));
                    opciones_spinner2.add(new Mes(i,
                            jsonObjRecibido.getJSONArray("select2").getJSONObject(i).getString("id"),
                            jsonObjRecibido.getJSONArray("select2").getJSONObject(i).getString("Usuario")));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_izq, spinner1_lista);
        spinner_bandejas.setAdapter(dataAdapter);

        spinner_bandejas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(check.isConnected(getActivity())){
                    id_baneja = opciones_spinner.get(i).obtenerId();
                    if(primeraVez){
                        TareaAsincrona tarea = new TareaAsincrona(1);
                        tarea.execute();
                    } else {
                        TareaAsincrona tarea = new TareaAsincrona(2);
                        tarea.execute();
                    }
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
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
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_izq, spinner2_lista);
        spinner_filtro.setAdapter(dataAdapter2);
        spinner_filtro.setSelection(0, false);

        spinner_filtro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(check.isConnected(getActivity())){
                    id_usuario2 = opciones_spinner2.get(i).getId_mes();

                    if(!primeraVez){
                        TareaAsincrona tarea = new TareaAsincrona(3);
                        tarea.execute();
                    }
                }else{

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        fbEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check.isConnected(getActivity())){
                    Intent i = new Intent(getActivity(), com.proj.agnus.activity.Email.class);
                    i.putExtra("activity_name","Fragment_Email");
                    startActivity(i);
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
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
        });

        swipeRefrescarEmail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TareaAsincrona tarea = new TareaAsincrona(1);
                tarea.execute();
            }
        });

        swipeRefrescarEmail.setColorSchemeColors(Color.parseColor("#307095"));

        return rootView;
    }


    public void llenarLista(int source){
        HashMap<String, String> parametros_email = new HashMap<>();

        switch (source){
            case 1: //Primer carga de pantalla (cargar spinner y recycler normal)
                parametros_email.put("fun","1");
                parametros_email.put("user",""+id_usuario);
                parametros_email.put("esc",""+id_escuela);

                jsonObjRecibido = conectar.connect(url_email, parametros_email, getActivity());
                primeraVez = false;

                break;

            case 2: //Spinner bandejas //No regresa select1 solo select2
                parametros_email.put("fun","2");
                parametros_email.put("user",""+id_usuario);
                parametros_email.put("esc",""+id_escuela);
                parametros_email.put("ban",""+id_baneja);

                jsonObjRecibido = conectar.connect(url_email, parametros_email, getActivity());
                spinner2_lista.clear();
                opciones_spinner2.clear();

                try {
                    if(jsonObjRecibido.getJSONArray("select2").length() == 0){
                        spinner2_lista.add("Sin filtros");
                    } else {
                        for(int i = 0; i < jsonObjRecibido.getJSONArray("select2").length(); i++){
                            spinner2_lista.add(jsonObjRecibido.getJSONArray("select2").getJSONObject(i).getString("Usuario"));
                            opciones_spinner2.add(new Mes(i,
                                    jsonObjRecibido.getJSONArray("select2").getJSONObject(i).getString("id"),
                                    jsonObjRecibido.getJSONArray("select2").getJSONObject(i).getString("Usuario")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case 3: //Spinner filtro //No regresa selectores, solo recycler
                parametros_email.put("fun","3");
                parametros_email.put("user",""+id_usuario);
                parametros_email.put("esc",""+id_escuela);
                parametros_email.put("ban",""+id_baneja);
                parametros_email.put("user2",""+id_usuario2);

                jsonObjRecibido = conectar.connect(url_email, parametros_email, getActivity());
                break;
        }

        lista.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail")){
                mostrarTexto = true;
            } else {
                for(int i = 0; i < jsonObjRecibido.getJSONArray("usuarios").length(); i++){
                    lista.add(new Email(
                            Integer.parseInt(jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("id")),
                            jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("Asunto"),
                            jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("img"),

                            (jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).has("leido")?
                                    jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("leido"): "1"),

                            jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("user"),
                            jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("fecha_corta"),
                            jsonObjRecibido.getJSONArray("usuarios").getJSONObject(i).getString("Mensaje")
                    ));
                }

                if(jsonObjRecibido.getJSONArray("usuarios").length() == 0){
                    mostrarTexto = true;
                } else {
                    mostrarTexto = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void eliminarEmail(int id_correo){
        HashMap<String, String> parametros_email = new HashMap<>();
        parametros_email.put("fun","4");
        parametros_email.put("esc",""+id_escuela);
        parametros_email.put("ban",""+id_baneja);
        parametros_email.put("id",""+id_correo);
        parametros_email.put("user",""+id_usuario);

        jsonObjRecibido = conectar.connect(url_email, parametros_email, getActivity());

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


    public void adaptadorLleno() {
        adapter = new Adaptador_Email(lista, getActivity(), getActivity(), Fragment_Email.this, new Adaptador_Email.OnItemClickListener() {

            @Override
            public void OnItemClick(Email email) {
                Intent i = new Intent(getActivity(), com.proj.agnus.activity.Email_Completo.class);
                i.putExtra("id_escuela",""+id_escuela);
                i.putExtra("bandeja",""+id_baneja);
                i.putExtra("user",""+id_usuario);
                i.putExtra("id_correo",""+email.obtenerId());

                startActivity(i);
            }
        });

        rvListaBandeja.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        TareaAsincrona tarea = new TareaAsincrona(1);
        tarea.execute();

    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(getActivity());
        int source;

        public TareaAsincrona(int source){
            this.source = source;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Espere un momento por favor\n\nDescargando contenido...");
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            llenarLista(source);
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            adaptadorLleno();

            if(mostrarTexto){
                txtNoCorreos.setVisibility(View.VISIBLE);
            } else {
                txtNoCorreos.setVisibility(View.GONE);
            }
            if(result) { pDialog.dismiss(); }

            if (swipeRefrescarEmail.isRefreshing()) {
                swipeRefrescarEmail.setRefreshing(false);
            }
        }
    }
}