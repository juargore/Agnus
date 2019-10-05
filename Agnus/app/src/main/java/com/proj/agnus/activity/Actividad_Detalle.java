package com.proj.agnus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.proj.agnus.adaptadores.Adaptador_Actividad;
import com.proj.agnus.comun.Actividad;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Actividad_Detalle extends Activity {

    ActionBar actionBar;
    String url_logo, codigo_alumno, id_actividad;
    SharedPreferences prefs;
    TextView txtABC;
    WebView webViewActDet;
    RecyclerView listaActDet;
    ArrayList<Actividad> lista;
    String url_actividad_detalle = "agnus.mx/app/evaluacion.php";
    int id_escuela, tipo_usuario, codigo_usuario;
    String nombre, descripcion;
    Adaptador_Actividad adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_detalle);

        //Personalizar ActionBar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Actividad");
        actionBar.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#307095")));


        txtABC = (TextView) findViewById(R.id.txtABC);
        webViewActDet = (WebView) findViewById(R.id.webViewActDet);
        listaActDet = (RecyclerView) findViewById(R.id.listaActDet);


        //Configuración del webView
        webViewActDet.setWebViewClient(new WebViewClient());
        webViewActDet.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                webViewActDet.getSettings().setJavaScriptEnabled(true);
                webViewActDet.getSettings().setLoadsImagesAutomatically(true);
                webViewActDet.getSettings().setDomStorageEnabled(true);
                webViewActDet.getSettings().setAllowFileAccess(true);
                webViewActDet.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
        });

        //Listener para poder descargar archivos adjuntos del webView
        webViewActDet.setDownloadListener(new DownloadListener() {
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

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaActDet.setLayoutManager(layoutManager);

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

        ImageView icono_escuela = new ImageView(Actividad_Detalle.this);
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
        lista = new ArrayList<>();

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

        jsonObjRecibido = conectar.connect(url_actividad_detalle, parametros_actividad_detalle, Actividad_Detalle.this);
        lista.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail")){
                //textVisible = true;
            } else {
                //textVisible = false;
                nombre = jsonObjRecibido.getString("nombre");
                descripcion = jsonObjRecibido.getString("descripcion");

                for(int i = 0; i < jsonObjRecibido.getJSONArray("lista").length(); i++){
                    if(jsonObjRecibido.getString("tipo").equals("libre") || jsonObjRecibido.getString("tipo").equals("examen")){
                        lista.add(new Actividad(i,
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("nombre"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("cal"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("color"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("archivo"),
                                null,
                                null,
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("status")
                        ));
                    } else{
                        lista.add(new Actividad(i,
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("nombre"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("cal"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("color"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("archivo"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("link"),
                                jsonObjRecibido.getJSONArray("lista").getJSONObject(i).getString("extension"),
                                null
                        ));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        txtABC.setText(nombre);

        webViewActDet.setWebViewClient(new WebViewClient());
        webViewActDet.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                webViewActDet.getSettings().setJavaScriptEnabled(true);
                webViewActDet.getSettings().setLoadsImagesAutomatically(true);
                webViewActDet.getSettings().setDomStorageEnabled(true);
                webViewActDet.getSettings().setAllowFileAccess(true);
                webViewActDet.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
        });

        //Listener para poder descargar archivos adjuntos del webView
        webViewActDet.setDownloadListener(new DownloadListener() {
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

        webViewActDet.loadData(descripcion, "text/html; charset=utf-8", "utf-8");


         adapter = new Adaptador_Actividad(lista, getApplicationContext(), new Adaptador_Actividad.OnItemClickListener() {

             @Override
             public void OnItemClick(Actividad actividad) {
                 if(actividad.getLink() != null && !actividad.getLink().equals("")){
                     String url = actividad.getLink();

                     Intent i = new Intent(Intent.ACTION_VIEW);
                     i.setData(Uri.parse(url));
                     Actividad_Detalle.this.startActivity(i);
                 }
             }
         });

        listaActDet.setAdapter(adapter);
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(Actividad_Detalle.this);

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
