package com.proj.agnus.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.activity.MainActivity;
import com.proj.agnus.adaptadores.Adaptador_Notificaciones;
import com.proj.agnus.comun.Notificacion;
import com.proj.agnus.conexion.connect_HttpsPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Notificaciones extends Fragment {

    View rootView;
    RecyclerView listaNotificaciones;
    ArrayList<Notificacion> lista = new ArrayList<>();
    public static Adaptador_Notificaciones adapter;
    String url_pantalla_notif = "agnus.mx/app/notificaciones.php";
    JSONObject jsonObjRecibido;
    connect_HttpsPost conectar = new connect_HttpsPost();
    SharedPreferences prefs;
    int id_escuela;
    String tipo_usuario, codigo_usuario;
    TextView txtNoNotificaciones;
    SwipeRefreshLayout swipeRefrescarNotificaciones;
    boolean mostrarTexto = false;

    public Fragment_Notificaciones(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        listaNotificaciones = (RecyclerView) rootView.findViewById(R.id.listaNotificaciones);
        txtNoNotificaciones = (TextView) rootView.findViewById(R.id.txtNoNotificaciones);
        swipeRefrescarNotificaciones = (SwipeRefreshLayout) rootView.findViewById
                (R.id.swipeRefrescarNotificaciones);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaNotificaciones.setLayoutManager(layoutManager);

        swipeRefrescarNotificaciones.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TareaAsincrona tarea = new TareaAsincrona();
                tarea.execute();
            }
        });

        swipeRefrescarNotificaciones.setColorSchemeColors(Color.parseColor("#307095"));

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    public void adaptadorLleno(){
        adapter = new Adaptador_Notificaciones(lista, getActivity(), getActivity(), Fragment_Notificaciones.this, new Adaptador_Notificaciones.OnItemClickListener() {
            @Override
            public void OnItemClick(Notificacion notificacion) {
                int id_notificacion = notificacion.obtenerId();

                //Marcar notificacion como leida
                HashMap<String, String> parametros_notif_leida = new HashMap<>();
                parametros_notif_leida.put("fun","2");
                parametros_notif_leida.put("esc",""+id_escuela);
                parametros_notif_leida.put("notif",""+id_notificacion);
                jsonObjRecibido = conectar.connect(url_pantalla_notif, parametros_notif_leida, getActivity());


                if(notificacion.getMensaje().equals(null) || notificacion.getMensaje().equals("") ){
                    //Ir a la pantalla de la notificacion
                    int area = Integer.parseInt(notificacion.getArea());
                    switch (area){
                        case 0: area = 2;break;     //0. Notificación = 2
                        case 2: area = 8; break;    //2. Asistencias = 8
                        case 4: area = 6; break;    //4. Evaluación = 6
                        case 6: area = 7; break;    //6. Email = 7
                        case 7: area = 4; break;    //7. Agenda = 4
                        case 9: area = 1; break;    //9. Noticias = 1
                        case 10: area = 3; break;   //10. Calendario = 3
                        case 11: area = 5; break;   //11. Horarios = 5
                    }

                    ((MainActivity)getActivity()).mostrar_fragment(area);
                } else {
                    Toast.makeText(getActivity(), ""+notificacion.getMensaje(), Toast.LENGTH_LONG).show();
                }

            }
        });

        listaNotificaciones.setAdapter(adapter);
    }

    public void llenarLista(){
        conectar = new connect_HttpsPost();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "0"));
        tipo_usuario = (prefs.getString("tipo_usuario", "0"));
        codigo_usuario = (prefs.getString("codigo_usuario", "0"));

        HashMap<String, String> parametros_notificaciones = new HashMap<>();
        parametros_notificaciones.put("fun","1");
        parametros_notificaciones.put("esc",""+id_escuela);
        parametros_notificaciones.put("tipo",""+tipo_usuario);
        parametros_notificaciones.put("codigo",""+codigo_usuario);

        jsonObjRecibido = conectar.connect(url_pantalla_notif, parametros_notificaciones, getActivity());
        lista.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail") || jsonObjRecibido.getJSONArray("notif").length() == 0){
                mostrarTexto = true;
            } else {
                mostrarTexto = false;
                for(int i = 0; i < jsonObjRecibido.getJSONArray("notif").length(); i++){
                    lista.add(new Notificacion(
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getInt("id"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("texto"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("icono"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("fecha"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("hora"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("area"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("leido"),
                            jsonObjRecibido.getJSONArray("notif").getJSONObject(i).getString("mensaje"))
                    );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void eliminarNotificacion(int id_notificacion){
        HashMap<String, String> parametros_email = new HashMap<>();
        parametros_email.put("fun","3");
        parametros_email.put("esc",""+id_escuela);
        parametros_email.put("notif",""+id_notificacion);

        jsonObjRecibido = conectar.connect(url_pantalla_notif, parametros_email, getActivity());

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
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
                txtNoNotificaciones.setVisibility(View.VISIBLE);
            } else {
                txtNoNotificaciones.setVisibility(View.GONE);
            }
            if(result) { pDialog.dismiss(); }
            if (swipeRefrescarNotificaciones.isRefreshing()) {
                swipeRefrescarNotificaciones.setRefreshing(false);
            }
        }
    }

}
