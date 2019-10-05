package com.proj.agnus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.conexion.check_internet_connection;

import java.io.IOException;

public class Noticia_Completa extends Activity {

    TextView txtTituloNoticiaCompleta,
             txtSubtituloNoticiaCompleta,
             txtFechaNoticiaCompleta;
    ImageView imgFotoNoticiaCompleta;
    ActionBar actionBar;
    String url_imagen, url_logo;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_completa);

        txtTituloNoticiaCompleta = (TextView) findViewById(R.id.txtTituloNoticiaCompleta);
        txtSubtituloNoticiaCompleta = (TextView) findViewById(R.id.txtSubtituloNoticiaCompleta);
        txtFechaNoticiaCompleta = (TextView) findViewById(R.id.txtFechaNoticiaCompleta);
        imgFotoNoticiaCompleta = (ImageView) findViewById(R.id.imgFotoNoticiaCompleta);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Noticia Completa");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#307095")));

        url_imagen = getIntent().getExtras().getString("imagen");
        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        url_logo = prefs.getString("logo", "");

        txtTituloNoticiaCompleta.setText(""+getIntent().getExtras().getString("titulo"));
        txtSubtituloNoticiaCompleta.setText(""+getIntent().getExtras().getString("subtitulo"));
        txtFechaNoticiaCompleta.setText(""+getIntent().getExtras().getString("fecha"));

        check_internet_connection check = new check_internet_connection();
        if(check.isConnected(Noticia_Completa.this)){
            TareaAsincrona tarea = new TareaAsincrona();
            tarea.execute();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Noticia_Completa.this);
            builder1.setMessage("No se logró conectar al servidor. Verifique su conexión e intente de nuevo");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

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

        ImageView icono_escuela = new ImageView(Noticia_Completa.this);
        icono_escuela.setPadding(0,30,0,30);
        icono_escuela.setImageDrawable(new BitmapDrawable(getResources(), logo));

        MenuItem item = menu1.getItem(0);
        item.setActionView(icono_escuela);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Bitmap> {

        ProgressDialog pDialog = new ProgressDialog(Noticia_Completa.this);

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Espere un momento por favor\n\nDescargando contenido...");
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Descargar_Imagenes di = new Descargar_Imagenes();
            Bitmap bm = null;

            try {
                bm = di.obtenerImagen(url_imagen);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bm;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgFotoNoticiaCompleta.setImageBitmap((bitmap));
            pDialog.dismiss();

        }
    }


}
