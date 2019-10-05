package com.proj.agnus.tabs;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Alumno_Evaluacion;
import com.proj.agnus.comun.Alumno_Evaluacion;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Tab_Calificacion extends Fragment {

    View rootView;
    static String codigo_alumno;
    static String curso;
    static String ciclo;
    private SharedPreferences prefs;
    private static int tipo_usuario, tipo_contenido;
    private static int id_escuela;
    private static int codigo_usuario;
    static Context context;
    static RecyclerView listaTabCalificacion;
    static ArrayList<Alumno_Evaluacion> lista_alumno_ev;
    public static Adaptador_Alumno_Evaluacion adapter;
    private static String url_datos_evaluacion = "agnus.mx/app/evaluacion.php";


    public Tab_Calificacion(int tipo_content, String c, String g, String codigoAlumno) {
        tipo_contenido = tipo_content;
        ciclo = c;
        curso = g;
        codigo_alumno = codigoAlumno;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tab__calificacion, container, false);

        listaTabCalificacion = (RecyclerView) rootView.findViewById(R.id.listaTabCalificacion);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaTabCalificacion.setLayoutManager(layoutManager);

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    public static void setValores(String _alumno, String _ciclo, String _curso){
        codigo_alumno = _alumno;
        ciclo = _ciclo;
        curso =  _curso;
        if(curso.equals("-1")){
            listaTabCalificacion.setVisibility(View.GONE);
        } else {
            listaTabCalificacion.setVisibility(View.VISIBLE);
            TareaAsincrona tarea = new TareaAsincrona();
            tarea.execute();
        }
    }

    public static void llenarLista(){
        JSONObject jsonObjRecibido;
        connect_HttpPost conectar = new connect_HttpPost();

        HashMap<String, String> parametros_contenido_evaluacion = new HashMap<>();
        parametros_contenido_evaluacion.put("fun","2");
        parametros_contenido_evaluacion.put("esc",""+id_escuela);
        parametros_contenido_evaluacion.put("tipo",""+tipo_usuario);

        if(tipo_usuario == 6){
            parametros_contenido_evaluacion.put("codigo", ""+codigo_alumno);
        }else{
            parametros_contenido_evaluacion.put("codigo", ""+codigo_usuario);
        }

        parametros_contenido_evaluacion.put("ciclo",""+ciclo);
        parametros_contenido_evaluacion.put("curso", ""+curso);

        Activity activity = (Activity) context;
        jsonObjRecibido = conectar.connect(url_datos_evaluacion, parametros_contenido_evaluacion, activity);

        lista_alumno_ev = new ArrayList<>();
        lista_alumno_ev.clear();
        try {
            for(int i = 0; i< jsonObjRecibido.getJSONArray("tabs").length(); i++){
                if(jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getInt("tipo") == 4){
                    for(int j = 0; j < jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("lista").length(); j++){
                        lista_alumno_ev.add(new Alumno_Evaluacion(j,
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("lista").getJSONObject(j).getString("nombre"),
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("lista").getJSONObject(j).getString("area"),
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("lista").getJSONObject(j).getString("cal"),
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("lista").getJSONObject(j).getString("color"),
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("lista").getJSONObject(j).getString("status"))
                        );
                    }
                } else if(jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getInt("numero") == 4 &&
                        jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getInt("tipo") == 2){

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void adaptadorLleno(){
        adapter = new Adaptador_Alumno_Evaluacion(lista_alumno_ev, new Adaptador_Alumno_Evaluacion.OnItemClickListener() {
            @Override
            public void OnItemClick(Alumno_Evaluacion alumno_evaluacion) {

            }
        });

        listaTabCalificacion.setAdapter(adapter);
    }


    private static class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(context);

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
        }
    }

}
