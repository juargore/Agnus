package com.proj.agnus.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_NavDrawer_List;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.comun.Imagen_Circular;
import com.proj.agnus.comun.Item_Menu;
import com.proj.agnus.conexion.connect_HttpPost;
import com.proj.agnus.fragments.Fragment_Agenda;
import com.proj.agnus.fragments.Fragment_Ajustes;
import com.proj.agnus.fragments.Fragment_Asistencias;
import com.proj.agnus.fragments.Fragment_Calendario;
import com.proj.agnus.fragments.Fragment_Email;
import com.proj.agnus.fragments.Fragment_Evaluacion;
import com.proj.agnus.fragments.Fragment_Horarios;
import com.proj.agnus.fragments.Fragment_Noticias;
import com.proj.agnus.fragments.Fragment_Notificaciones;
import com.proj.agnus.seguridad.Login1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("ResourceType")
public class MainActivity extends FragmentActivity {

    Bitmap bm;
    ActionBar actionBar;
    DrawerLayout layout_menu_lateral;
    ArrayList<Item_Menu> items_menu;
    Adaptador_NavDrawer_List adapter;
    ActionBarDrawerToggle icono_actionBar_menu;
    CharSequence almacen_titulo_pantalla_actual;
    CharSequence titulo_pantalla_actual;
    String[] titulos_menu_lateral;
    TypedArray iconos_menu_lateral;
    ListView lista_menu_lateral;
    JSONArray modulos, nuevosModulos;
    String nombre_usuario, url_foto_usuario, url_logo;
    int tipo_usuario, cambio_imagen = 0, posicion = 0;
    String codigo_usuario, id_esc, nombre_esc;
    TareaAsincrona tarea;
    Descargar_Imagenes di;
    SharedPreferences prefs;
    String url_pantalla_login = "agnus.mx/app/login.php";
    String url_pantalla_token = "agnus.mx/app/token.php";
    String nombre_activity ="";
    JSONObject jsonObjRecibido;
    boolean doubleBackToExit = false;
    connect_HttpPost conectar;
    String noticias = "0", notificaciones = "0",
            calendario = "0", agenda = "0",
            evaluacion = "0", email = "0",
            asistencias = "0";


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Match componentes XML
        layout_menu_lateral = (DrawerLayout) findViewById(R.id.layout_menu_lateral);
        lista_menu_lateral = (ListView) findViewById(R.id.lista_menu_lateral);

        //Propiedades del Action bar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.icono_menu2);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#307095")));

        //Tomar los valores que hay en Shared Preferences
        conectar = new connect_HttpPost();
        di = new Descargar_Imagenes();
        prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        nombre_usuario = prefs.getString("nombre", "");
        url_foto_usuario = prefs.getString("foto", "");
        codigo_usuario = prefs.getString("codigo_usuario", "");
        id_esc = prefs.getString("id_escuela", "");
        nombre_esc = prefs.getString("nombre_escuela", "");
        url_logo = prefs.getString("logo", "");

        
        //Propiedades del ActionBar y el menu lateral
        icono_actionBar_menu = new ActionBarDrawerToggle(this, layout_menu_lateral,
                R.drawable.icono_menu, R.string.app_name,R.string.app_name) {
            public void onDrawerClosed(View view) { invalidateOptionsMenu(); }
            public void onDrawerOpened(View drawerView) { invalidateOptionsMenu(); }
        };

        layout_menu_lateral.setDrawerListener(icono_actionBar_menu);
        almacen_titulo_pantalla_actual = titulo_pantalla_actual = getTitle();

        //Si la pantalla viene del Splash Screen (Ya estaba loggeado)
        nombre_activity = getIntent().getExtras().getString("activity_name");
        Log.e("Nombre Activity:", ""+nombre_activity);

        if(nombre_activity.equals("Splash")) {
            //descargar_numeros_menu_lateral, y luego
            //colocar_items_menu_lateral
            tarea = new TareaAsincrona(1);
            tarea.execute();

            //descargar_imagen_del_header
            //colocar_imagen_al_header
            //mostrar_fragment de Noticias
            tarea = new TareaAsincrona(0);
            tarea.execute();
        }

        //Si la pantalla viene de haber hecho Login apenas
        else if (nombre_activity.equals("Login") || nombre_activity.equals("Notificacion_Local") || nombre_activity.equals("notificacion_push")){

            //Obtener números del menú lateral
            if(getIntent().getExtras().containsKey("noticias")){
                noticias = getIntent().getExtras().getString("noticias");
            }
            if(getIntent().getExtras().containsKey("notificaciones")){
                notificaciones = getIntent().getExtras().getString("notificaciones");
            }
            if(getIntent().getExtras().containsKey("calendario")){
                calendario = getIntent().getExtras().getString("calendario");
            }
            if(getIntent().getExtras().containsKey("agenda")){
                agenda = getIntent().getExtras().getString("agenda");
            }
            if(getIntent().getExtras().containsKey("evaluacion")){
                evaluacion = getIntent().getExtras().getString("evaluacion");
            }
            if(getIntent().getExtras().containsKey("email")){
                email = getIntent().getExtras().getString("email");
            }
            if(getIntent().getExtras().containsKey("asistencias")) {
                asistencias = getIntent().getExtras().getString("asistencias");
            }

            //Obtener los modulos que se mostrarán en el menú lateral
            if(getIntent().getExtras().containsKey("modulos")) {
                String str_array = getIntent().getExtras().getString("modulos");

                try {

                    modulos = new JSONArray(str_array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //descargar_imagen_del_header, luego colocar_imagen_al_header
            //colocar_items_menu_lateral, luego mostrar_fragment de Noticias
            tarea = new TareaAsincrona(0);
            tarea.execute();
        }
    }

    public void colocar_items_en_menu_lateral(){
        items_menu = new ArrayList<>();
        titulos_menu_lateral = getResources().getStringArray(R.array.array_titulos_menu_lateral);
        iconos_menu_lateral = getResources().obtainTypedArray(R.array.array_iconos_menu_lateral);

        if (modulos.toString().contains("noticias")) {
            items_menu.add(new Item_Menu(1, titulos_menu_lateral[0], iconos_menu_lateral.getResourceId(0, -1), true, String.valueOf(noticias))); //Noticias
        }

        if (modulos.toString().contains("notificaciones")) {
            items_menu.add(new Item_Menu(2, titulos_menu_lateral[1], iconos_menu_lateral.getResourceId(1, -1), true, String.valueOf(notificaciones))); //Notificaciones
        }

        if (modulos.toString().contains("calendario")) {
            items_menu.add(new Item_Menu(3, titulos_menu_lateral[2], iconos_menu_lateral.getResourceId(2, -1), true, String.valueOf(calendario))); //Calendario
        }

        if (modulos.toString().contains("agenda")) {
            items_menu.add(new Item_Menu(4, titulos_menu_lateral[3], iconos_menu_lateral.getResourceId(3, -1), true, String.valueOf(agenda))); //Agenda
        }

        if (modulos.toString().contains("horarios")) {
            items_menu.add(new Item_Menu(5, titulos_menu_lateral[4], iconos_menu_lateral.getResourceId(4, -1), true, "0")); //Horario
        }

        if (modulos.toString().contains("evaluacion")) {
            items_menu.add(new Item_Menu(6, titulos_menu_lateral[5], iconos_menu_lateral.getResourceId(5, -1), true, String.valueOf(evaluacion))); //Evaluacion
        }

        if (modulos.toString().contains("email")) {
            items_menu.add(new Item_Menu(7, titulos_menu_lateral[6], iconos_menu_lateral.getResourceId(6, -1), true, String.valueOf(email))); //Email
        }

        if (modulos.toString().contains("asistencias")) {
            items_menu.add(new Item_Menu(8, titulos_menu_lateral[7], iconos_menu_lateral.getResourceId(7, -1), true, String.valueOf(asistencias))); //Asistencias
        }

        items_menu.add(new Item_Menu(9, titulos_menu_lateral[8], iconos_menu_lateral.getResourceId(8, -1), true, "0")); //Ajustes
        items_menu.add(new Item_Menu(10, titulos_menu_lateral[9], iconos_menu_lateral.getResourceId(9, -1), true, "0")); //Salir

        iconos_menu_lateral.recycle();
        adapter = new Adaptador_NavDrawer_List(getApplicationContext(), items_menu, tipo_usuario);
        lista_menu_lateral.setAdapter(adapter);

        lista_menu_lateral.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("Pos1",""+items_menu.get(i-1).getPosition());

                mostrar_fragment(items_menu.get(i-1).getPosition());
            }
        });
    }

    public void colocar_imagen_al_header(){
        final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        final View header = inflater.inflate(R.layout.header_design, (ViewGroup)findViewById(R.id.llaayy));

        lista_menu_lateral.removeHeaderView(header);
        lista_menu_lateral.addHeaderView(header, null, false);
        final TextView txtUsuario = (TextView)header.findViewById(R.id.tv);
        final TextView txtPerfil = (TextView)header.findViewById(R.id.txtPerfil);
        final TextView txtEscuelaHeader = (TextView)header.findViewById(R.id.txtEscuelaHeader);
        txtUsuario.setText(nombre_usuario.toString());
        txtPerfil.setText(""+nombre_tipo_usuario(tipo_usuario));
        txtEscuelaHeader.setText(""+nombre_esc);

        Point size = new Point();
        int width = size.x;
        int tam = width/4;

        final ImageView imgUsuario = (ImageView)header.findViewById(R.id.iv1);
        Imagen_Circular ic = new Imagen_Circular();
        Bitmap b = ic.getCircleBitmap(bm, MainActivity.this);

        imgUsuario.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgUsuario.setImageBitmap(b);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgUsuario.setBackground(drawCircle(tam, tam, Color.WHITE));
        }
        //Grosor del borde
        imgUsuario.setPadding(7, 7, 7, 7);
    }

    public void descargar_imagen_del_header(){
        try {
            if(url_foto_usuario.equals("") || url_foto_usuario == null){
                url_foto_usuario = "https://agnus.mx/landing/images/logo.png";
            }
            bm = di.obtenerImagen(url_foto_usuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void descargar_imagen_del_header_actualizada(){
        HashMap<String, String> parametros_autenticacion = new HashMap<>();
        parametros_autenticacion.put("fun","3");
        parametros_autenticacion.put("user", codigo_usuario);
        parametros_autenticacion.put("esc", id_esc);
        parametros_autenticacion.put("tipo", ""+tipo_usuario);

        jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_autenticacion, MainActivity.this);

        try {
            url_foto_usuario = jsonObjRecibido.getString("avatar");
            bm = di.obtenerImagen(url_foto_usuario);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Colocar logo de la escuela en ActionBar
        Bitmap logo = null;
        Menu menu1 = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        try {
            logo = di.obtenerImagen(url_logo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView icono_escuela = new ImageView(MainActivity.this);
        icono_escuela.setPadding(0,30,0,30);
        icono_escuela.setImageDrawable(new BitmapDrawable(getResources(), logo));

        MenuItem item = menu1.getItem(0);
        item.setActionView(icono_escuela);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Si el menu lateral esta abierto y se presiona el boton de menu, no hacer nada...
        if(layout_menu_lateral.isDrawerOpen(lista_menu_lateral)){

        } else {
            //Checar por actualizaciones en los numeros laterales y en las celdas
            TareaAsincrona tareaAsincrona = new TareaAsincrona(1);
            tareaAsincrona.execute();
        }

        if (icono_actionBar_menu.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(layout_menu_lateral.isDrawerOpen(lista_menu_lateral)){
            layout_menu_lateral.closeDrawer(lista_menu_lateral);
        } else {
            if (doubleBackToExit) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExit = true;
            Toast.makeText(this, "Presione nuevamete el botón ATRAS para salir de la App", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExit = false;
                }
            }, 2000);
        }
    }

    public void mostrar_fragment(int position) {
        if(nombre_activity.equals("notificacion_push")){
            posicion = Integer.parseInt(getIntent().getExtras().getString("area"));

            switch (posicion){
                case 0: posicion = 2; break;
                case 2: posicion = 8; break;
                case 4: posicion = 6; break;
                case 6: posicion = 7; break;
                case 7: posicion = 4; break;
                case 9: posicion = 1; break;
                case 10: posicion = 3; break;
                case 11: posicion = 5; break;
            }

            for (int i = 0; i<items_menu.size(); i++){
                if(items_menu.get(i).getPosition() == posicion){
                    position = posicion;
                    break;
                } else {
                    position = 2;
                }
            }

            nombre_activity = "";
        }

        Fragment fragment = null;
        android.support.v4.app.FragmentManager fra = getSupportFragmentManager();

        Log.e("Pos2",""+position);
        switch (position) {
            case 1: fragment = new Fragment_Noticias(); break;
            case 2: fragment = new Fragment_Notificaciones(); break;
            case 3: fragment = new Fragment_Calendario(); break;
            case 4: fragment = new Fragment_Agenda(); break;
            case 5: fragment = new Fragment_Horarios(fra, getApplicationContext()); break;
            case 6: fragment = new Fragment_Evaluacion(fra); break;
            case 7: fragment = new Fragment_Email(); break;
            case 8: fragment = new Fragment_Asistencias(fra, getApplicationContext()); break;
            case 9: fragment = new Fragment_Ajustes(); break;
            case 10:
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Cerrando sesión en Agnus\n\nEspere por favor...");
                dialog.setCancelable(false);
                dialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        SharedPreferences.Editor editor = getSharedPreferences("Agnus_BD", MODE_PRIVATE).edit();
                        editor.putBoolean("recordar_contrasena", false);
                        editor.commit();

                        //Desligar Token
                        desligar_token();
                        dialog.dismiss();

                        Intent finalizar = new Intent(MainActivity.this, Login1.class);
                        MainActivity.this.finish();
                        startActivity(finalizar);
                    }
                }, 3000);
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_contenedor, fragment).commit();

            lista_menu_lateral.setItemChecked(position, true);
            lista_menu_lateral.setSelection(position);
            //actionBar.setTitle(items_menu.get(position).getTitulo());
            actionBar.setTitle(titulos_menu_lateral[position-1]);
            layout_menu_lateral.closeDrawer(lista_menu_lateral);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public void desligar_token(){
        SharedPreferences prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        HashMap<String, String> parametros_token = new HashMap<>();
        parametros_token.put("fun","3");
        parametros_token.put("esc",""+prefs.getString("id_escuela","0"));
        parametros_token.put("token",""+prefs.getString("token","0"));
        jsonObjRecibido = conectar.connect(url_pantalla_token, parametros_token, MainActivity.this);
    }

    public static ShapeDrawable drawCircle (int width, int height, int color) {
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight (height);
        oval.setIntrinsicWidth (width);
        oval.getPaint ().setColor (color);
        return oval;
    }

    public String nombre_tipo_usuario(int tipo_user){
        switch (tipo_user){
            case 1: return "Administrador";
            case 2: return "Profesor";
            case 3: return "Alumno";
            case 4: return "Administrativo";
            case 5: return "Operativo";
            default: return "Padre de familia";
        }
    }

    public void descargar_numeros_menu_lateral(){
        HashMap<String, String> parametros_autenticacion = new HashMap<>();
        parametros_autenticacion.put("fun","4");
        parametros_autenticacion.put("user", codigo_usuario);
        parametros_autenticacion.put("esc", id_esc);
        parametros_autenticacion.put("tipo", ""+tipo_usuario);

        jsonObjRecibido = conectar.connect(url_pantalla_login, parametros_autenticacion, MainActivity.this);

        try {
            if(jsonObjRecibido.get("status").equals("success")){

                //Numeros del menu lateral
                if(jsonObjRecibido.has("cambio_imagen")){
                    cambio_imagen = jsonObjRecibido.getInt("cambio_imagen"); }
                if(jsonObjRecibido.has("noticias")){
                    noticias = jsonObjRecibido.getString("noticias"); }
                if(jsonObjRecibido.has("notificaciones")){
                    notificaciones = jsonObjRecibido.getString("notificaciones"); }
                if(jsonObjRecibido.has("calendario")){
                    calendario = jsonObjRecibido.getString("calendario"); }
                if(jsonObjRecibido.has("agenda")){
                    agenda = jsonObjRecibido.getString("agenda"); }
                if(jsonObjRecibido.has("evaluacion")){
                    evaluacion = jsonObjRecibido.getString("evaluacion"); }
                if(jsonObjRecibido.has("email")){
                    email = jsonObjRecibido.getString("email"); }
                if(jsonObjRecibido.has("asistencias")){
                    asistencias = jsonObjRecibido.getString("asistencias"); }

                /*modulos = new JSONArray();
                modulos.put("agenda");
                modulos.put("notificaciones");
                modulos.put("evaluacion");
                modulos.put("noticias");*/

                modulos = jsonObjRecibido.getJSONArray("modulos");

            } else {
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                });*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
        int procedencia;

        public TareaAsincrona(int procedencia){
            this.procedencia = procedencia;
        }

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
            if(procedencia == 0){
                descargar_imagen_del_header();
            } else if (procedencia == 1){
                descargar_numeros_menu_lateral();
            } else if (procedencia == 2){
                descargar_imagen_del_header_actualizada();
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
            if(procedencia == 0){
                colocar_items_en_menu_lateral();
                colocar_imagen_al_header();

                if(nombre_activity.equals("Login") || nombre_activity.equals("Splash")){

                    //Viene desde Splash o Login, mostrar primer Fragment de la lista
                    mostrar_fragment(items_menu.get(0).getPosition());
                } else {
                    mostrar_fragment(2); //Viene de una Notificacion. Mostrar Fragment
                }
            } else if(procedencia == 1){
                colocar_items_en_menu_lateral();

                if(cambio_imagen == 1){
                    TareaAsincrona tareaAsincrona = new TareaAsincrona(2);
                    tareaAsincrona.execute();
                    cambio_imagen = 0;
                }
            } else if (procedencia == 2){
                colocar_imagen_al_header();
            }

            if(result) { pDialog.dismiss(); }
        }
    }
}
