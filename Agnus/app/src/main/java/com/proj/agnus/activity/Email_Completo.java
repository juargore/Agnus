package com.proj.agnus.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Email_Completo extends Activity {

    ActionBar actionBar;
    WebView webViewMsj;
    TextView txtAsuntoEmailC;
    private JSONObject jsonObjRecibido;
    public connect_HttpPost conectar;
    String id_escuela, bandeja, id_correo, id_usuario, id_correo2;
    final String url_email = "agnus.mx/app/correo.php";
    String contenido = "", asunto = "", tipo = "", url_logo, adjunto;
    FloatingActionButton fbResponderEmail;
    List<String> lista_id_user;
    ProgressBar progressBarEmail;
    Button ibBackEmail;
    SharedPreferences prefs;
    LinearLayout layoutBtnRegresar;
    static final int REQUEST_READ_PHONE_STATE = 1;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_completo);

        //Personalizar ActionBar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Mensaje");
        actionBar.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#307095")));

        //Parámetros que vienen del fragment_Email
        id_escuela = getIntent().getExtras().getString("id_escuela");
        bandeja    = getIntent().getExtras().getString("bandeja");
        id_correo  = getIntent().getExtras().getString("id_correo");
        id_usuario = getIntent().getExtras().getString("user");
        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        url_logo = prefs.getString("logo", "");

        //Solicitar permiso de WRITE_EXTERNAL_STORAGE
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_READ_PHONE_STATE);
        }

        //Match de componentes XML
        fbResponderEmail = (FloatingActionButton) findViewById(R.id.fbResponderEmail);
        txtAsuntoEmailC = (TextView) findViewById(R.id.txtAsuntoEmailC);
        progressBarEmail = (ProgressBar) findViewById(R.id.progressBarEmail);
        layoutBtnRegresar = (LinearLayout) findViewById(R.id.layoutBtnRegresar);
        webViewMsj = (WebView) findViewById(R.id.webViewMsj);
        ibBackEmail = (Button) findViewById(R.id.ibBackEmail);

        //Configuración del webView
        webViewMsj.setWebViewClient(new WebViewClient());
        webViewMsj.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                progressBarEmail.setVisibility(View.VISIBLE);
                webViewMsj.getSettings().setJavaScriptEnabled(true);
                webViewMsj.getSettings().setLoadsImagesAutomatically(true);
                webViewMsj.getSettings().setDomStorageEnabled(true);
                webViewMsj.getSettings().setAllowFileAccess(true);
                webViewMsj.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                progressBarEmail.setProgress(progress);
                if (progress == 100) {
                    progressBarEmail.setVisibility(View.GONE);
                }
            }
        });

        //Listener para poder descargar archivos adjuntos del webView
        webViewMsj.setDownloadListener(new DownloadListener() {
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

        //Botón para regresar a la página principal del Email
        ibBackEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webViewMsj.loadDataWithBaseURL(null, contenido, "text/html", "utf-8", null);
            }
        });

        //Floating Button para responder mensaje
        fbResponderEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ir = new Intent(Email_Completo.this, Email.class);
                ir.putExtra("activity_name","Respuesta_Email");
                ir.putExtra("asunto", ""+asunto);
                ir.putExtra("select", ""+tipo);
                ir.putExtra("id_correo", ""+id_correo2);
                ir.putStringArrayListExtra("lista_destinatarios", (ArrayList<String>) lista_id_user);
                startActivity(ir);
            }
        });

        //Descargar JSON con información del mensaje a detalle
        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();
    }

    //Revisar si los permisos fueron concedidos para WRITE_EXTERNAL_STORAGE
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    //Refrescar Activity al responder msj
    @Override
    public void onResume() {
        super.onResume();
        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

    }

    //Icono de Agnus a la derecha
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

        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        ImageView icono_escuela = new ImageView(Email_Completo.this);
        //icono_escuela.setScaleType(ImageView.ScaleType.FIT_XY);
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

    //Tarea asíncrona para descargar los datos del email a detalle
    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(Email_Completo.this);

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
            HashMap<String, String> parametros_email = new HashMap<>();
            parametros_email.put("fun","5");
            parametros_email.put("ban",""+bandeja);
            parametros_email.put("esc",""+id_escuela);
            parametros_email.put("id",""+id_correo);
            parametros_email.put("user",""+id_usuario);

            conectar = new connect_HttpPost();
            jsonObjRecibido = conectar.connect(url_email, parametros_email, Email_Completo.this);
            try {
                adjunto = jsonObjRecibido.getString("Adjunto");
                contenido = jsonObjRecibido.getString("correo").replace("http://","https://");
                asunto = jsonObjRecibido.getString("Asunto");
                tipo = jsonObjRecibido.getString("select");
                id_correo2 = jsonObjRecibido.getString("id_correo");

                lista_id_user = new ArrayList<>();

                for(int i=0; i<jsonObjRecibido.getJSONArray("respuesta").length(); i++){
                    lista_id_user.add((String) jsonObjRecibido.getJSONArray("respuesta").get(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
            webViewMsj.loadDataWithBaseURL(null, contenido, "text/html", "utf-8", null);
            txtAsuntoEmailC.setText(asunto);


            //Mostar u ocultar icono de Inicio si hay Adjuntos
            if(adjunto.equals("1")){
                layoutBtnRegresar.setVisibility(View.VISIBLE);
            } else {
                layoutBtnRegresar.setVisibility(View.GONE);
            }

            //Habilitar o deshabilitar floatingButton si es Admin
            if(tipo.equals("5")){
                fbResponderEmail.setEnabled(false);
                fbResponderEmail.setClickable(false);
                fbResponderEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80307095")));
            }

            if(result) { pDialog.dismiss(); }
        }
    }
}
