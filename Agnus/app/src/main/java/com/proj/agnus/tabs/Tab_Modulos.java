package com.proj.agnus.tabs;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.activity.Actividad_Detalle;
import com.proj.agnus.activity.Activity_Debate;
import com.proj.agnus.adaptadores.Adaptador_Exp_Actividades;
import com.proj.agnus.adaptadores.Adaptador_Exp_Descripcion;
import com.proj.agnus.adaptadores.Adaptador_Exp_Recursos;
import com.proj.agnus.comun.Actividades;
import com.proj.agnus.comun.Alumno;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.comun.Recursos;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Tab_Modulos extends Fragment {

    View rootView;
    static String codigo_alumno;
    static String curso;
    static String ciclo;
    private SharedPreferences prefs;
    private static int tipo_usuario;
    private static int id_escuela;
    private static int codigo_usuario;
    static Context context;
    private static String url_datos_evaluacion = "agnus.mx/app/evaluacion.php";
    static LinearLayout layPrincipal;
    static List<Alumno> nombre;
    static List<Periodo> descripcion;
    static List<Recursos> recursos;
    static List<Actividades> actividades;
    static check_internet_connection check;

    public Tab_Modulos(String c, String g, String codigoAlumno) {
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

        rootView = inflater.inflate(R.layout.fragment_tab__modulos, container, false);
        layPrincipal = (LinearLayout) rootView.findViewById(R.id.layPrincipal);

        check = new check_internet_connection();
        llenarLista(context);
        return rootView;
    }

    public static void setValores(String _alumno, String _ciclo, String _curso){
        codigo_alumno = _alumno;
        ciclo = _ciclo;
        curso =  _curso;
        if(curso.equals("-1")){
            layPrincipal.setVisibility(View.GONE);
        } else {
            layPrincipal.setVisibility(View.VISIBLE);
            llenarLista(context);
        }
    }

    public static void llenarLista(final Context context){
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
        JSONArray modulos = new JSONArray();

        try {
            for(int i = 0; i< jsonObjRecibido.getJSONArray("tabs").length(); i++){
                if(jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getInt("tipo") == 3){
                    modulos = jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getJSONArray("modulos");
                }
            }

            //Si existe view, limpiarlo
            layPrincipal.removeAllViews();

            for(int i = 0; i<modulos.length(); i++){
                nombre = new ArrayList<>();
                descripcion = new ArrayList<>();
                recursos = new ArrayList<>();
                actividades = new ArrayList<>();

                nombre.add(new Alumno(i, modulos.getJSONObject(i).getString("nombre")));
                descripcion.add(new Periodo(i, modulos.getJSONObject(i).getString("descripcion").toString()));

                JSONArray a = modulos.getJSONObject(i).getJSONArray("recursos");
                for (int k = 0; k<a.length(); k++){
                    String descripcion;

                    if(a.getJSONObject(k).has("descripcion")){
                        descripcion = ""+a.getJSONObject(k).getString("descripcion");
                    }else{
                        descripcion = "";
                    }

                    recursos.add(new Recursos(i,
                            ""+a.getJSONObject(k).getString("titulo"),
                            descripcion,
                            ""+a.getJSONObject(k).getString("tipo"),
                            ""+a.getJSONObject(k).getString("enlace"),
                            ""+a.getJSONObject(k).getString("label")));
                }

                JSONArray b = modulos.getJSONObject(i).getJSONArray("actividades");
                for (int h = 0; h<b.length(); h++){
                    int id = 0;
                    if(b.getJSONObject(h).getString("id").equals("") || b.getJSONObject(h).getString("id").equals(null)){
                        id = -1;
                    } else {
                        id = b.getJSONObject(h).getInt("id");
                    }
                    actividades.add(new Actividades(id,
                            b.getJSONObject(h).getString("titulo"),
                            b.getJSONObject(h).getString("tipo"),
                            b.getJSONObject(h).getString("label")));
                }


                for(int j = 0; j<modulos.getJSONObject(i).length(); j++){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    //Crear los TextViews de Header y RecycerView dinamicamente
                    final LinearLayout ll = new LinearLayout(context);
                    ll.setLayoutParams(params);
                    params.setMargins(10, 0, 10, 0);
                    ll.setOrientation(LinearLayout.VERTICAL);

                    final TextView txtTitulo = new TextView(context);
                    txtTitulo.setBackgroundColor(Color.parseColor("#F2F3F4"));
                    txtTitulo.setTextColor(Color.parseColor("#0D3863"));
                    txtTitulo.setTextSize((float) 14.0);
                    txtTitulo.setPadding(60,20,0,20);
                    txtTitulo.setTypeface(Typeface.DEFAULT_BOLD);
                    txtTitulo.setLayoutParams(params);
                    ll.addView(txtTitulo);

                    //Crear RecyclerView
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                    final RecyclerView rvLista = new RecyclerView(context);
                    rvLista.setLayoutParams(params);
                    rvLista.setLayoutManager(layoutManager);
                    rvLista.setNestedScrollingEnabled(false);

                    if(j == 0){
                        //Nombre del modulo
                        txtTitulo.setText(" "+modulos.getJSONObject(i).getString("nombre").toUpperCase());
                        txtTitulo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        txtTitulo.setBackgroundColor(Color.parseColor("#E5E7E9"));
                        txtTitulo.setTextSize((float) 16.0);
                        txtTitulo.setEnabled(false);
                        params.setMargins(10, 5, 10, 1);
                        txtTitulo.setPadding(60,10,0,10);
                        txtTitulo.setLayoutParams(params);

                    } else if(j == 1){
                        txtTitulo.setText("+\t\tDESCRIPCIÓN");
                        Adaptador_Exp_Descripcion adapter = new Adaptador_Exp_Descripcion(descripcion, context);
                        rvLista.setAdapter(adapter);
                    } else if(j == 2){
                        txtTitulo.setText("+\t\tRECURSOS");
                        Adaptador_Exp_Recursos adapter = new Adaptador_Exp_Recursos(recursos, new Adaptador_Exp_Recursos.OnItemClickListener() {
                            @Override
                            public void OnItemClick(Recursos recursos) {
                                Activity activity1 = (Activity) context;

                                if(check.isConnected(activity1)){
                                    String url;
                                    if(recursos.getEnlace().equals("") || recursos.getEnlace() == null){
                                        url = "0";
                                    } else{
                                        url = ""+recursos.getEnlace();
                                    }

                                    if(!url.equals("0")){
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        context.startActivity(i);
                                    }
                                } else {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity1);
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
                        });
                        rvLista.setAdapter(adapter);
                    } else if(j == 3){
                        txtTitulo.setText("+\t\tACTIVIDADES");
                        Adaptador_Exp_Actividades adapter = new Adaptador_Exp_Actividades(actividades, new Adaptador_Exp_Actividades.OnItemClickListener() {
                            @Override
                            public void OnItemClick(Actividades actividades) {
                                Activity activity1 = (Activity) context;

                                if(check.isConnected(activity1)){
                                    int id_actividad = actividades.obtenerId();
                                    String tipo_actividad;

                                    if(id_actividad > 0){
                                        tipo_actividad = actividades.getTipo();
                                        if(tipo_actividad.equals("debate")){
                                            Intent pantalla = new Intent(context.getApplicationContext(), Activity_Debate.class);
                                            pantalla.putExtra("id_actividad",""+id_actividad);
                                            if(tipo_usuario == 6){
                                                pantalla.putExtra("codigo_alumno",""+codigo_alumno);
                                            } else {
                                                pantalla.putExtra("codigo_alumno","000000");
                                            }
                                            context.startActivity(pantalla);
                                        } else {
                                            Intent pantalla = new Intent(context.getApplicationContext(), Actividad_Detalle.class);
                                            pantalla.putExtra("id_actividad",""+id_actividad);
                                            if(tipo_usuario == 6){
                                                pantalla.putExtra("codigo_alumno",""+codigo_alumno);
                                            } else {
                                                pantalla.putExtra("codigo_alumno","000000");
                                            }
                                            context.startActivity(pantalla);
                                        }
                                    }
                                } else{
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity1);
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
                        });
                        rvLista.setAdapter(adapter);
                    }

                    ll.addView(rvLista);
                    rvLista.setVisibility(View.GONE);

                    //Add view to Main Layout
                    layPrincipal.addView(ll);

                    //Efecto de lista desplegable al dar click en titulo
                    txtTitulo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(rvLista.getVisibility() == View.VISIBLE){
                                rvLista.setVisibility(View.GONE);
                                txtTitulo.setText("+"+txtTitulo.getText().toString().substring(1));
                            } else {
                                rvLista.setVisibility(View.VISIBLE);
                                txtTitulo.setText("—"+txtTitulo.getText().toString().substring(1));
                            }
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
