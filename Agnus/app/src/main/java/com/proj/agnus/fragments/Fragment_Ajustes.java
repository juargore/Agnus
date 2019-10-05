package com.proj.agnus.fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proj.agnus.R;
import com.proj.agnus.activity.Ajustes_Detalle;
import com.proj.agnus.adaptadores.Adaptador_Ajustes;
import com.proj.agnus.comun.Ajustes;
import com.proj.agnus.conexion.connect_HttpsPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Ajustes extends Fragment {

    View rootView;
    RecyclerView listaAjustes;
    SwipeRefreshLayout swipeRefrescarAjustes;
    connect_HttpsPost conectar;
    SharedPreferences prefs;
    int id_escuela;
    public Adaptador_Ajustes adapter;
    ArrayList<Ajustes> lista_ajustes;
    String tipo_usuario, codigo_usuario;
    String url_pantalla_ajustes = "agnus.mx/app/ajustes.php";
    JSONObject jsonObjRecibido;

    public Fragment_Ajustes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ajustes, container, false);
        listaAjustes = (RecyclerView) rootView.findViewById(R.id.listaAjustes);
        swipeRefrescarAjustes = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefrescarAjustes);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaAjustes.setLayoutManager(layoutManager);

        swipeRefrescarAjustes.setColorSchemeColors(Color.parseColor("#307095"));
        swipeRefrescarAjustes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TareaAsincrona tarea = new TareaAsincrona();
                tarea.execute();
            }
        });

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    public void llenarLista(){
        conectar = new connect_HttpsPost();
        lista_ajustes = new ArrayList<>();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "0"));
        tipo_usuario = (prefs.getString("tipo_usuario", "0"));
        codigo_usuario = (prefs.getString("codigo_usuario", "0"));

        HashMap<String, String> parametros_ajustes = new HashMap<>();
        parametros_ajustes.put("fun","1");
        parametros_ajustes.put("esc",""+id_escuela);
        parametros_ajustes.put("tipo",""+tipo_usuario);
        parametros_ajustes.put("codigo",""+codigo_usuario);

        jsonObjRecibido = conectar.connect(url_pantalla_ajustes, parametros_ajustes, getActivity());
        lista_ajustes.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail")){

            } else {

                for(int i = 0; i < jsonObjRecibido.getJSONArray("modulos").length(); i++){
                    lista_ajustes.add(new Ajustes(
                            jsonObjRecibido.getJSONArray("modulos").getJSONObject(i).getInt("id"),
                            jsonObjRecibido.getJSONArray("modulos").getJSONObject(i).getString("nombre"),
                            jsonObjRecibido.getJSONArray("modulos").getJSONObject(i).getString("icono"),
                            jsonObjRecibido.getJSONArray("modulos").getJSONObject(i).getString("subtitulo"))
                    );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno() {
        adapter = new Adaptador_Ajustes(lista_ajustes, new Adaptador_Ajustes.OnItemClickListener() {
            @Override
            public void OnItemClick(Ajustes ajustes) {
                Intent detalle = new Intent(getActivity(), Ajustes_Detalle.class);
                detalle.putExtra("modulo", ""+ajustes.obtenerId());
                startActivity(detalle);
            }
        });

        listaAjustes.setAdapter(adapter);
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Espere un momento por favor\n\nDescargando contenido...");
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            llenarLista();
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

            if(result) { pDialog.dismiss(); }
            if (swipeRefrescarAjustes.isRefreshing()) {
                swipeRefrescarAjustes.setRefreshing(false);
            }
        }
    }

}
