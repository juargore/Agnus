package com.proj.agnus.fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Noticias;
import com.proj.agnus.comun.Noticias;
import com.proj.agnus.conexion.connect_HttpsPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Noticias extends Fragment {

    View pantalla;
    RecyclerView listadoNoticias;
    public static Adaptador_Noticias adapter;
    ArrayList<Noticias> lista = new ArrayList<>();
    TextView txtNoNoticias;
    boolean mostrarTexto = false;

    public Fragment_Noticias(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pantalla = inflater.inflate(R.layout.fragment_noticias, container, false);
        listadoNoticias = (RecyclerView) pantalla.findViewById(R.id.listadoNoticias);
        txtNoNoticias = (TextView)pantalla.findViewById(R.id.txtNoNoticias);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listadoNoticias.setLayoutManager(layoutManager);

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return pantalla;
    }

    public void adaptadorLleno(){
        adapter = new Adaptador_Noticias(lista, getActivity(), new Adaptador_Noticias.OnItemClickListener() {
            @Override
            public void OnItemClick(Noticias noticia) {
                int position = noticia.obtenerId();

                Intent i = new Intent(getActivity(), com.proj.agnus.activity.Noticia_Completa.class);
                i.putExtra("titulo",""+lista.get(position).obtenerNombre());
                i.putExtra("subtitulo",""+lista.get(position).getDescripcion());
                i.putExtra("fecha",""+lista.get(position).getFecha());
                i.putExtra("imagen",""+lista.get(position).getImagen());
                startActivity(i);
            }
        });

        listadoNoticias.setAdapter(adapter);
    }

    public void llenarLista(){

        String url_pantalla_login = "agnus.mx/app/noticias.php";
        JSONObject jsonObjRecibido;
        connect_HttpsPost conectar = new connect_HttpsPost();

        SharedPreferences prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        int id_escuela = Integer.parseInt(prefs.getString("id_escuela", "0"));

        HashMap<String, String> parametros_escuelas = new HashMap<>();
        parametros_escuelas.put("fun","1");
        parametros_escuelas.put("esc",""+id_escuela);

        jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_escuelas, getActivity());

        try {
            if(jsonObjRecibido.get("status").equals("fail")){
                mostrarTexto = true;
            } else {
                for(int i = 0; i < jsonObjRecibido.getJSONArray("noticias").length(); i++){
                    lista.add(new Noticias(i,
                            jsonObjRecibido.getJSONArray("noticias").getJSONObject(i).getString("titulo"),
                            jsonObjRecibido.getJSONArray("noticias").getJSONObject(i).getString("descrip"),
                            jsonObjRecibido.getJSONArray("noticias").getJSONObject(i).getString("fecha"),
                            jsonObjRecibido.getJSONArray("noticias").getJSONObject(i).getString("imagen")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            if(mostrarTexto){
                txtNoNoticias.setVisibility(View.VISIBLE);
            }
            if(result) { pDialog.dismiss(); }
        }
    }
}
