package com.proj.agnus.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.activity.Asistencia_Detalle;
import com.proj.agnus.adaptadores.Adaptador_Asistencias;
import com.proj.agnus.comun.Asistencia_Alumno;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Mes_Asistencias extends Fragment {

    static final String ARG_POSITION = "position";
    static final String ARG_ID_UAC = "id_uac";
    RecyclerView listaAsistencias;
    String numero_mes;
    View rootView;
    int id_uac, id_escuela, codigo_usuario;
    TextView txtNoAsistencias;
    String url_listado = "agnus.mx/app/asistencias.php";
    ArrayList<Asistencia_Alumno> lista = new ArrayList<>();
    public static ArrayList<Asistencia_Alumno> lista1 = new ArrayList<>();
    public Adaptador_Asistencias adapter;
    private SharedPreferences prefs;
    private int tipo_usuario;
    private SwipeRefreshLayout swipeRefreshAsis;

    public Fragment_Mes_Asistencias() {}

    public static Fragment_Mes_Asistencias nuevaInstancia(String mes, int id_uac) {
        Fragment_Mes_Asistencias fragment = new Fragment_Mes_Asistencias();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITION, mes);
        args.putSerializable(ARG_ID_UAC, id_uac);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numero_mes = getArguments().getString(ARG_POSITION);
        id_uac = getArguments().getInt(ARG_ID_UAC);

        //Obtener datos de Shared Preferences
        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mes_asistencias, container, false);
        listaAsistencias = (RecyclerView) rootView.findViewById(R.id.listaAsistencias);
        swipeRefreshAsis = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefrescarAsis);
        txtNoAsistencias = (TextView) rootView.findViewById(R.id.txtNoAsistencias);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaAsistencias.setLayoutManager(layoutManager);

        swipeRefreshAsis.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TareaAsincrona tarea = new TareaAsincrona();
                tarea.execute();
            }
        });

        swipeRefreshAsis.setColorSchemeColors(Color.parseColor("#307095"));

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == 10001)) {

            TareaAsincrona tarea = new TareaAsincrona();
            tarea.execute();
        }
    }

    public void llenarLista(){
        JSONObject jsonObjRecibido;
        connect_HttpPost conectar = new connect_HttpPost();

        HashMap<String, String> parametros_eventos_calendario = new HashMap<>();
        if(tipo_usuario == 2){ //Profesor
            parametros_eventos_calendario.put("fun","2");
            parametros_eventos_calendario.put("uac",""+id_uac);
        } else { //Padre o Alumno
            parametros_eventos_calendario.put("fun","4");
            parametros_eventos_calendario.put("user",""+id_uac);}

        parametros_eventos_calendario.put("esc",""+id_escuela);
        parametros_eventos_calendario.put("fecha",""+numero_mes);

        jsonObjRecibido = conectar.connect(url_listado, parametros_eventos_calendario, getActivity());
        lista = new ArrayList<>();
        //lista.clear();

        try {
            if(tipo_usuario == 2){ //Profesor
                for(int i = 0; i < jsonObjRecibido.getJSONArray("datos").length(); i++){
                    lista.add(new Asistencia_Alumno(i,
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("alumno"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("codigo"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("status"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("tot_asis_alum"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("porcentaje"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("color"),
                            1, "","","","","","",1)
                    );
                }

            } else {
                for(int i = 0; i < jsonObjRecibido.getJSONArray("datos").length(); i++){
                    lista.add(new Asistencia_Alumno(i,
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("uac"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("profesor"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("oferta"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("tot_asis"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("porcentaje"),
                            jsonObjRecibido.getJSONArray("datos").getJSONObject(i).getString("color"),
                            1, "","","","","","",1)
                    );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        if(lista.size() > 0){
            txtNoAsistencias.setVisibility(View.GONE);
            listaAsistencias.setVisibility(View.VISIBLE);

            adapter = new Adaptador_Asistencias(lista, getActivity(), new Adaptador_Asistencias.OnItemClickListener() {
                @Override
                public void OnItemClick(Asistencia_Alumno asistencia_alumno) {

                    Intent abrir = new Intent(getActivity(), Asistencia_Detalle.class);
                    abrir.putExtra("nombre_alumno_materia",""+asistencia_alumno.obtenerNombre());

                    if(tipo_usuario == 2){
                        abrir.putExtra("user",""+asistencia_alumno.getCodigo());
                        abrir.putExtra("uac",""+id_uac);
                    } else {
                        abrir.putExtra("user",""+id_uac);
                        abrir.putExtra("uac",""+asistencia_alumno.getStatus());
                    }
                    abrir.putExtra("id_escuela",""+id_escuela);
                    abrir.putExtra("fecha",""+numero_mes);
                    startActivityForResult(abrir, 10001);
                }
            }, tipo_usuario, 1);

            listaAsistencias.setAdapter(adapter);

        } else {
            txtNoAsistencias.setVisibility(View.VISIBLE);
            listaAsistencias.setVisibility(View.GONE);
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
            if(result) { pDialog.dismiss(); }
            if (swipeRefreshAsis.isRefreshing()) {
                swipeRefreshAsis.setRefreshing(false);
            }

            //Lista estatica para enviar a Agregar Asistencia
            lista1 = lista;
        }
    }
}
