package com.proj.agnus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Foro;
import com.proj.agnus.comun.Alumno_Foro;
import com.proj.agnus.comun.Comentario_Foro;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Debate extends Activity {

    ActionBar actionBar;
    String url_logo, codigo_alumno, id_actividad;
    SharedPreferences prefs;
    TextView txtNombreActDeb;
    WebView webViewDebate;
    RecyclerView listaDebate;
    ArrayList<Alumno_Foro> alumno_foro;
    ArrayList<Comentario_Foro> comentario_foro;
    String url_actividad_detalle = "agnus.mx/app/evaluacion.php";
    int id_escuela, tipo_usuario, codigo_usuario;
    String nombre, descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debate);

        //Personalizar ActionBar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        }
        actionBar.setTitle("Debate");
        actionBar.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#307095")));

        txtNombreActDeb = (TextView) findViewById(R.id.txtNombreActDeb);
        webViewDebate = (WebView) findViewById(R.id.webViewDebate);
        listaDebate = (RecyclerView) findViewById(R.id.listaDebate);

        //Configuración del webView
        webViewDebate.setWebViewClient(new WebViewClient());
        webViewDebate.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                webViewDebate.getSettings().setJavaScriptEnabled(true);
                webViewDebate.getSettings().setLoadsImagesAutomatically(true);
                webViewDebate.getSettings().setDomStorageEnabled(true);
                webViewDebate.getSettings().setAllowFileAccess(true);
                webViewDebate.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
        });

        //Listener para poder descargar archivos adjuntos del webView
        webViewDebate.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Agnus: Descarga de archivos");

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Descargando archivo...", Toast.LENGTH_LONG).show();
            }
        });

        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        url_logo = prefs.getString("logo", "");
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));
        codigo_alumno = getIntent().getExtras().getString("codigo_alumno");
        id_actividad = getIntent().getExtras().getString("id_actividad");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaDebate.setNestedScrollingEnabled(false);
        listaDebate.setLayoutManager(layoutManager);

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();
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

        ImageView icono_escuela = new ImageView(Activity_Debate.this);
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
            finish();
        }
        return true;
    }


    public void llenarLista(){
        JSONObject jsonObjRecibido;
        connect_HttpPost conectar = new connect_HttpPost();
        alumno_foro = new ArrayList<>();
        comentario_foro = new ArrayList<>();

        HashMap<String, String> parametros_actividad_detalle = new HashMap<>();
        parametros_actividad_detalle.put("fun","3");
        parametros_actividad_detalle.put("esc",""+id_escuela);
        parametros_actividad_detalle.put("tipo",""+tipo_usuario);

        if(tipo_usuario == 6){
            parametros_actividad_detalle.put("codigo", ""+codigo_alumno);
        }else{
            parametros_actividad_detalle.put("codigo", ""+codigo_usuario);
        }

        parametros_actividad_detalle.put("actividad",""+id_actividad);

        jsonObjRecibido = conectar.connect(url_actividad_detalle, parametros_actividad_detalle, Activity_Debate.this);
        alumno_foro.clear();
        comentario_foro.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail")){

            } else {
                nombre = jsonObjRecibido.getString("nombre");
                descripcion = jsonObjRecibido.getString("descripcion");

                for(int i = 0; i < jsonObjRecibido.getJSONArray("lista").length(); i++){

                    JSONObject jsonObject = jsonObjRecibido.getJSONArray("lista").getJSONObject(i);

                    comentario_foro = new ArrayList<>();
                    for(int j = 0; j < jsonObject.getJSONArray("respuestas").length(); j++){
                        comentario_foro.add(new Comentario_Foro(i,
                                jsonObject.getJSONArray("respuestas").getJSONObject(j).getString("nombre"),
                                jsonObject.getJSONArray("respuestas").getJSONObject(j).getString("fecha"),
                                jsonObject.getJSONArray("respuestas").getJSONObject(j).getString("texto")
                            ));
                    }

                    alumno_foro.add(new Alumno_Foro(
                            jsonObject.getString("nombre")+"-"+
                                    jsonObject.getString("cal")+"-"+
                                    jsonObject.getString("color"),
                            comentario_foro)
                    );

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        txtNombreActDeb.setText(nombre);
        webViewDebate.loadData(descripcion, "text/html; charset=utf-8", "utf-8");

        Adaptador_Foro adapter = new Adaptador_Foro(getApplication(), alumno_foro);
        listaDebate.setAdapter(adapter);
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(Activity_Debate.this);

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
        protected void onPostExecute(Boolean b) {
            adaptadorLleno();
            pDialog.dismiss();

        }
    }

}
