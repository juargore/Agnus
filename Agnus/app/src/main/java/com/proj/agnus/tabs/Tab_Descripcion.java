package com.proj.agnus.tabs;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.proj.agnus.R;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Tab_Descripcion extends Fragment {

    View rootView;
    static WebView webViewEvaluacion;
    private SharedPreferences prefs;
    private static int tipo_usuario;
    private static int id_escuela;
    private static int codigo_usuario;
    private static String url_datos_evaluacion = "agnus.mx/app/evaluacion.php";
    private static String contenido;
    static Context context;
    static String codigo_alumno;
    static String curso;
    static String ciclo;
    private static ProgressBar progressBarDesc;

    public Tab_Descripcion(String c, String g, String codigoAlumno) {
        ciclo = c;
        curso = g;
        codigo_alumno = codigoAlumno;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener datos de Shared Preferences
        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_descripcion, container, false); 
        
        webViewEvaluacion = (WebView) rootView.findViewById(R.id.webViewTabEvaluacion);
        progressBarDesc = (ProgressBar) rootView.findViewById(R.id.progressBarDesc);

        //Configuración del webView
        webViewEvaluacion.setWebViewClient(new WebViewClient());
        webViewEvaluacion.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                progressBarDesc.setVisibility(View.VISIBLE);
                webViewEvaluacion.getSettings().setJavaScriptEnabled(true);
                webViewEvaluacion.getSettings().setLoadsImagesAutomatically(true);
                webViewEvaluacion.getSettings().setDomStorageEnabled(true);
                webViewEvaluacion.getSettings().setAllowFileAccess(true);
                webViewEvaluacion.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                progressBarDesc.setProgress(progress);
                    if (progress == 100) {
                        progressBarDesc.setVisibility(View.GONE);
                    }
            }
        });

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    public static void setValores(String _alumno, String _ciclo, String _curso){
        codigo_alumno = _alumno;
        ciclo = _ciclo;
        curso =  _curso;

        if(curso.equals("-1")){
            webViewEvaluacion.setVisibility(View.GONE);
            progressBarDesc.setVisibility(View.GONE);
        } else {
            webViewEvaluacion.setVisibility(View.VISIBLE);
            progressBarDesc.setVisibility(View.VISIBLE);
            TareaAsincrona tarea = new TareaAsincrona();
            tarea.execute();
        }
    }


    public static void llenarLista(){
        JSONObject jsonObjRecibido;
        connect_HttpPost conectar = new connect_HttpPost();

        HashMap<String, String> parametros_contenido_descripcion = new HashMap<>();
        parametros_contenido_descripcion.put("fun","2");
        parametros_contenido_descripcion.put("esc",""+id_escuela);
        parametros_contenido_descripcion.put("tipo",""+tipo_usuario);

        if(tipo_usuario == 6){
            parametros_contenido_descripcion.put("codigo", ""+codigo_alumno);
        }else{
            parametros_contenido_descripcion.put("codigo", ""+codigo_usuario);
        }

        parametros_contenido_descripcion.put("ciclo",""+ciclo);
        parametros_contenido_descripcion.put("curso", ""+curso);

        Activity activity = (Activity) context;
        jsonObjRecibido = conectar.connect(url_datos_evaluacion, parametros_contenido_descripcion, activity);

        try {
            for(int j = 0; j< jsonObjRecibido.getJSONArray("tabs").length(); j++){
                if(jsonObjRecibido.getJSONArray("tabs").getJSONObject(j).getInt("tipo") == 1){
                    contenido = jsonObjRecibido.getJSONArray("tabs").getJSONObject(j).getString("descripcion").replace("http://","https://");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            webViewEvaluacion.loadDataWithBaseURL(null, contenido, "text/html", "utf-8", null);
            if(result) { pDialog.dismiss(); }
        }
    }

}
