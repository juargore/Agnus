package com.proj.agnus.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.activity.Agregar_Asistencia;
import com.proj.agnus.adaptadores.Adaptador_Tabs_Asistencias;
import com.proj.agnus.comun.Mes;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Asistencias extends Fragment implements Serializable{

    private TabLayout tabsMesesAsistencias;
    private ViewPager viewPagerAsistencias;
    private FragmentManager frMng;
    private ArrayList<Periodo> periodos;
    private ArrayList<Mes> meses;
    private Spinner spinnerAlumno_Uac_Asis;
    private RelativeLayout rLSpinnerAsistencias;
    private EditText etNombreCicloAsistencias;
    private ImageView ivFlechitaAbajo;
    private connect_HttpPost conectar;
    private JSONObject jsonObjRecibido;
    private String url_spinner_asis = "agnus.mx/app/asistencias.php";
    private String fecha = "";
    private Context context;
    private View rootView;
    private ImageView ivAgregar;
    private SharedPreferences prefs;
    private int tipo_usuario, id_escuela, codigo_usuario, id_uac;
    private TextView txtAlumno_Uac_Asis;
    public static String nombre_uac;
    List<String> elementos_spinner_asistencias;


    public Fragment_Asistencias(){}

    @SuppressLint("ValidFragment")
    public Fragment_Asistencias(FragmentManager fra, Context context){
        frMng = fra;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_asistencias, container, false);

        txtAlumno_Uac_Asis = (TextView) rootView.findViewById(R.id.txtAlumno_Uac_Asis);
        spinnerAlumno_Uac_Asis = (Spinner) rootView.findViewById(R.id.spinnerAlumno_Uac_Asis);
        rLSpinnerAsistencias = (RelativeLayout) rootView.findViewById(R.id.rLSpinnerAsistencias);
        etNombreCicloAsistencias = (EditText) rootView.findViewById(R.id.etNombreCicloAsistencias);
        ivFlechitaAbajo = (ImageView) rootView.findViewById(R.id.ivFlechitaAbajo);
        tabsMesesAsistencias = (TabLayout) rootView.findViewById(R.id.tabsMesesAsistencias);
        viewPagerAsistencias = (ViewPager) rootView.findViewById(R.id.viewpagerAsistencias);
        ivAgregar = (ImageView) rootView.findViewById(R.id.ivAgregar);

        //Obtener datos de Shared Preferences
        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));

        conectar = new connect_HttpPost();
        elementos_spinner_asistencias = new ArrayList<>();
        periodos = new ArrayList<>();
        meses = new ArrayList<>();

        if(tipo_usuario == 2){
            ivAgregar.setVisibility(View.VISIBLE);
        } else {
            ivAgregar.setVisibility(View.GONE);
        }

        ivAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Agregar_Asistencia.class);
                i.putExtra("id_esc", ""+id_escuela);
                i.putExtra("id_uac", ""+id_uac);

                if(fecha.equals(null) || fecha.equals("")){
                    i.putExtra("fecha", ""+meses.get(0).getId_mes());
                } else{
                    i.putExtra("fecha", ""+fecha);
                }
                i.putExtra("nombre_uac", ""+nombre_uac);
                startActivity(i);
            }
        });

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
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
            HashMap<String, String> parametros_spinner_asistencias = new HashMap<>();

            if(tipo_usuario == 2){ //Profesor
                parametros_spinner_asistencias.put("fun","1");
            } else { //Padre o Alumno
                parametros_spinner_asistencias.put("fun","3");
            }
            parametros_spinner_asistencias.put("esc",""+id_escuela);
            parametros_spinner_asistencias.put("tipo",""+tipo_usuario);
            parametros_spinner_asistencias.put("user",""+codigo_usuario);

            jsonObjRecibido = conectar.connect(url_spinner_asis, parametros_spinner_asistencias, getActivity());

            try {
                if(tipo_usuario == 2){ //USUARIO = PROFESOR
                    for(int i = 0; i < jsonObjRecibido.getJSONObject("select").getJSONArray("select").length(); i++){
                        elementos_spinner_asistencias.add(jsonObjRecibido.getJSONObject("select").getJSONArray("select").getJSONObject(i).getString("opcion"));
                        periodos.add(new Periodo(
                                Integer.parseInt(jsonObjRecibido.getJSONObject("select").getJSONArray("select").getJSONObject(i).getString("id")),
                                jsonObjRecibido.getJSONObject("select").getJSONArray("select").getJSONObject(i).getString("opcion")));
                    }

                } else { //USUARIO = PADRE O ALUMNO
                    for(int i = 0; i < jsonObjRecibido.getJSONObject("select").getJSONArray("alumnos").length(); i++){
                        elementos_spinner_asistencias.add(jsonObjRecibido.getJSONObject("select").getJSONArray("alumnos").getJSONObject(i).getString("opcion"));
                        periodos.add(new Periodo(
                                Integer.parseInt(jsonObjRecibido.getJSONObject("select").getJSONArray("alumnos").getJSONObject(i).getString("id")),
                                jsonObjRecibido.getJSONObject("select").getJSONArray("alumnos").getJSONObject(i).getString("opcion")));
                    }
                }

                //==LLENAR OBJECT DE MES PARA PASARLO A LOS TABS==
                for(int i = 0; i < jsonObjRecibido.getJSONObject("tap").getJSONArray("id").length(); i++){
                    meses.add(new Mes(i,
                            jsonObjRecibido.getJSONObject("tap").getJSONArray("id").get(i).toString(),
                            jsonObjRecibido.getJSONObject("tap").getJSONArray("mes").get(i).toString().replaceAll(".(?=.)", "$0 ")));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if(result) { pDialog.dismiss(); }

            try {
                txtAlumno_Uac_Asis.setText(jsonObjRecibido.getJSONObject("select").getString("label"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //==AGREGAR EL LISTADO DEL ARRAY AL SPINNER
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, elementos_spinner_asistencias);
            spinnerAlumno_Uac_Asis.setAdapter(dataAdapter);
            if(elementos_spinner_asistencias.size() < 2){
                ivFlechitaAbajo.setVisibility(View.GONE);
            }

            spinnerAlumno_Uac_Asis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if(new check_internet_connection().isConnected(getActivity())){
                        id_uac = periodos.get(i).obtenerId();
                        nombre_uac = periodos.get(i).obtenerNombre();

                        //==CREAR LOS TABS DINAMICAMENTE==
                        Adaptador_Tabs_Asistencias adapter = new Adaptador_Tabs_Asistencias(getActivity(), frMng, meses, id_uac);
                        viewPagerAsistencias.setAdapter(adapter);
                        viewPagerAsistencias.setOffscreenPageLimit(meses.size());
                        tabsMesesAsistencias.setupWithViewPager(viewPagerAsistencias);

                        //==COLOCAR TAB EN MES ACTUAL==
                        Calendar c = Calendar.getInstance();
                        int mes_calendar = 1 + c.get(Calendar.MONTH);
                        int mes_json;
                        int posicion_tab = 0;

                        for (int k=0; k<meses.size(); k++){
                            String mes = meses.get(k).getId_mes();
                            mes = mes.substring(mes.length() - 2);

                            mes_json = Integer.parseInt(mes); //08... 11... 12

                            if(mes_json == mes_calendar){
                                posicion_tab = k;
                                break;
                            }
                        }

                        //==SI NO EXISTE EL MES, SE PONE EL PRIMERO POR DEFAULT==
                        if(posicion_tab < meses.size()){
                            TabLayout.Tab tab = tabsMesesAsistencias.getTabAt(posicion_tab);
                            tab.select();
                        }

                    }else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
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
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            viewPagerAsistencias.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    fecha = meses.get(position).getId_mes();
                    if(tipo_usuario == 2){
                        if(viewPagerAsistencias.getCurrentItem() == (meses.size()-1)){
                            ivAgregar.setVisibility(View.GONE);
                        } else{
                            ivAgregar.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
