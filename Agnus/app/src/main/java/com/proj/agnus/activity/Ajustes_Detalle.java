package com.proj.agnus.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.proj.agnus.R;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.fragments.Fragment_Ajustes_Detalle;
import com.proj.agnus.fragments.Fragment_Ajustes_Password;

import java.io.IOException;

public class Ajustes_Detalle extends FragmentActivity {

    String url_logo;
    ActionBar actionBar;
    SharedPreferences prefs;
    Fragment_Ajustes_Detalle fragment_ajustes_detalle = null;
    Fragment_Ajustes_Password fragment_ajustes_password = null;
    String modulo;
    LinearLayout fragment_principal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_detalle);

        //Personalizar ActionBar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        actionBar.setTitle("Ajustes");
        actionBar.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#307095")));

        fragment_principal = (LinearLayout) findViewById(R.id.fragment_principal);
        fragment_principal.setVisibility(View.VISIBLE);

        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        url_logo = prefs.getString("logo", "");
        modulo = getIntent().getExtras().getString("modulo");

        Bundle bundle = new Bundle();
        bundle.putString("modulo", ""+modulo);

        check_internet_connection check = new check_internet_connection();

        if(check.isConnected(Ajustes_Detalle.this)){
            if(modulo.equals("0")){
                fragment_ajustes_password = new Fragment_Ajustes_Password();
                fragment_ajustes_password.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().
                        add(R.id.fragment_principal, fragment_ajustes_password, "Ajustes_Password").commit();
            } else {
                fragment_ajustes_detalle = new Fragment_Ajustes_Detalle();
                fragment_ajustes_detalle.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().
                        add(R.id.fragment_principal, fragment_ajustes_detalle, "Ajustes_Detalle").commit();
            }
        }else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Ajustes_Detalle.this);
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

        ImageView icono_escuela = new ImageView(Ajustes_Detalle.this);
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

}
