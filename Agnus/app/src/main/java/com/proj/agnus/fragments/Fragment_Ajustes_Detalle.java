package com.proj.agnus.fragments;



import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Ajustes_Detalle;
import com.proj.agnus.comun.Ajustes_Detalles;
import com.proj.agnus.conexion.connect_HttpsPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Ajustes_Detalle extends Fragment {

    View rootView;
    RecyclerView listaAjustesDetalle;
    connect_HttpsPost conectar;
    SharedPreferences prefs;
    String url_pantalla_ajustes = "agnus.mx/app/ajustes.php";
    JSONObject jsonObjRecibido;
    int id_escuela;
    String tipo_usuario, codigo_usuario, modulo;
    List<Ajustes_Detalles> lista_ajustes;
    Adaptador_Ajustes_Detalle adapter;

    public Fragment_Ajustes_Detalle() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ajustes_detalle, container, false);
        listaAjustesDetalle = (RecyclerView) rootView.findViewById(R.id.listaAjustesDetalle);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaAjustesDetalle.setLayoutManager(layoutManager);

        Bundle filtro = this.getArguments();
        if (filtro != null) {
            modulo = filtro.getString("modulo");
        }

        TareaAsincrona tarea = new TareaAsincrona(false);
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
        parametros_ajustes.put("fun","2");
        parametros_ajustes.put("esc",""+id_escuela);
        parametros_ajustes.put("tipo",""+tipo_usuario);
        parametros_ajustes.put("codigo",""+codigo_usuario);
        parametros_ajustes.put("modulo",""+modulo);

        jsonObjRecibido = conectar.connect(url_pantalla_ajustes, parametros_ajustes, getActivity());
        lista_ajustes.clear();

        try {
            if(jsonObjRecibido.get("status").equals("success")){
                for(int i = 0; i < jsonObjRecibido.getJSONArray("items").length(); i++){
                    lista_ajustes.add(new Ajustes_Detalles(
                            jsonObjRecibido.getJSONArray("items").getJSONObject(i).getInt("id"),
                            jsonObjRecibido.getJSONArray("items").getJSONObject(i).getString("nombre"),
                            jsonObjRecibido.getJSONArray("items").getJSONObject(i).getInt("valor"))
                    );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno() {
        adapter = new Adaptador_Ajustes_Detalle(lista_ajustes, new Adaptador_Ajustes_Detalle.OnItemClickListener() {
            @Override
            public void OnItemClick(Ajustes_Detalles ajustes) {
                int id_item = ajustes.obtenerId();

                TareaAsincrona tarea = new TareaAsincrona(true, id_item);
                tarea.execute();
            }
        });

        listaAjustesDetalle.setAdapter(adapter);
    }

    public void enviarEstadoSwitch(int id_item){
        conectar = new connect_HttpsPost();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "0"));
        tipo_usuario = (prefs.getString("tipo_usuario", "0"));
        codigo_usuario = (prefs.getString("codigo_usuario", "0"));

        HashMap<String, String> parametros_ajustes = new HashMap<>();
        parametros_ajustes.put("fun","3");
        parametros_ajustes.put("esc",""+id_escuela);
        parametros_ajustes.put("tipo",""+tipo_usuario);
        parametros_ajustes.put("codigo",""+codigo_usuario);
        parametros_ajustes.put("item",""+id_item);

        conectar.connect(url_pantalla_ajustes, parametros_ajustes, getActivity());
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(getActivity());
        private boolean esActualizarSwitch;
        private int id_item;

        public TareaAsincrona(boolean esActualizarSwitch){
            this.esActualizarSwitch = esActualizarSwitch;
        }

        public TareaAsincrona(boolean esActualizarSwitch, int id_item) {
            this.esActualizarSwitch = esActualizarSwitch;
            this.id_item = id_item;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if(esActualizarSwitch)
                pDialog.setMessage("Guardando configuración...");
            else
                pDialog.setMessage("Espere un momento por favor\n\nDescargando contenido...");

            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(esActualizarSwitch){
                enviarEstadoSwitch(id_item);
            } else {
                llenarLista();
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
            if(esActualizarSwitch){

            } else {
                adaptadorLleno();
            }

            if(result) { pDialog.dismiss(); }
        }
    }

}
