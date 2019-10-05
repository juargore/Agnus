package com.proj.agnus.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Tabs_Evaluacion;
import com.proj.agnus.comun.Alumno;
import com.proj.agnus.comun.Escuelas;
import com.proj.agnus.comun.Modulo;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;
import com.proj.agnus.tabs.Tab_Calificacion;
import com.proj.agnus.tabs.Tab_Descripcion;
import com.proj.agnus.tabs.Tab_Evaluacion;
import com.proj.agnus.tabs.Tab_Evaluacion_a;
import com.proj.agnus.tabs.Tab_Modulos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Evaluacion extends Fragment {

    View rootView;
    private TabLayout tabsModulosEvaluacion;
    private ViewPager viewPagerEvaluacion;
    private FragmentManager frMng;
    private SharedPreferences prefs;
    private connect_HttpPost conectar;
    private ArrayList<Alumno> alumno;
    private ArrayList<Escuelas> ciclo;
    private ArrayList<Periodo> grupo;
    private ArrayList<Modulo> modulos;
    String grupoE="0", cicloE="0", codigoAlumnoE="0";
    private int tipo_usuario, id_escuela, codigo_usuario;
    boolean pVCiclo = true, pvGrupo = true, pvAlumno = true;
    List<String> elementos_spinner_alumno;
    List<String> elementos_spinner_grupo;
    List<String> elementos_spinner_ciclo;
    check_internet_connection check;
    private LinearLayout linearLayoutHH, layHijosEvaluacion, layAgrupacionEvaluacion;
    private String url_spinner_evaluacion = "agnus.mx/app/evaluacion.php";
    private TextView txtCicloEvaluacion, txtGrupoEvaluacion, txtNoCursosEvaluacion;
    private Spinner spinnerCicloEvaluacion, spinnerGrupoEvaluacion, spinnerHijosEvaluacion;
    public static boolean existeEvaluacionA = false, existeDescripcion = false,
            existeEvaluacion = false, existeModulos =false, existeCalificacion = false;

    public Fragment_Evaluacion(FragmentManager fm){
        this.frMng = fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_evaluacion, container, false);

        tabsModulosEvaluacion = (TabLayout) rootView.findViewById(R.id.tabsModulosEvaluacion);
        viewPagerEvaluacion = (ViewPager) rootView.findViewById(R.id.viewPagerEvaluacion);
        layHijosEvaluacion = (LinearLayout) rootView.findViewById(R.id.layHijosEvaluacion);
        layAgrupacionEvaluacion = (LinearLayout) rootView.findViewById(R.id.layAgrupacionEvaluacion);
        txtCicloEvaluacion = (TextView) rootView.findViewById(R.id.txtCicloEvaluacion);
        txtGrupoEvaluacion = (TextView) rootView.findViewById(R.id.txtGrupoEvaluacion);
        txtNoCursosEvaluacion = (TextView) rootView.findViewById(R.id.txtNoCursosEvaluacion);
        spinnerCicloEvaluacion = (Spinner) rootView.findViewById(R.id.spinnerCicloEvaluacion);
        spinnerGrupoEvaluacion = (Spinner) rootView.findViewById(R.id.spinnerGrupoEvaluacion);
        spinnerHijosEvaluacion = (Spinner) rootView.findViewById(R.id.spinnerHijosEvaluacion);
        linearLayoutHH = (LinearLayout) rootView.findViewById(R.id.linearLayoutHH);

        //Obtener datos de Shared Preferences
        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));

        HashMap<String, String> parametros_spinner_evaluacion = new HashMap<>();
        parametros_spinner_evaluacion.put("fun","1");
        parametros_spinner_evaluacion.put("esc",""+id_escuela);
        parametros_spinner_evaluacion.put("tipo",""+tipo_usuario);
        parametros_spinner_evaluacion.put("codigo",""+codigo_usuario);

        conectar = new connect_HttpPost();
        check = new check_internet_connection();
        JSONObject jsonObjRecibido = conectar.connect(url_spinner_evaluacion, parametros_spinner_evaluacion, getActivity());

        try {
            if(jsonObjRecibido.get("status").equals("success")){
                llenarSpinners(jsonObjRecibido);
                llenarTabs(jsonObjRecibido);
            } else {
                linearLayoutHH.setVisibility(View.GONE);
                txtNoCursosEvaluacion.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    public void llenarSpinners(final JSONObject jsonObjRecibido){

        try {
            if(jsonObjRecibido.get("status").equals("success")){

                //Llenar los TextView
                if(jsonObjRecibido.has("ciclo")){
                    txtCicloEvaluacion.setText(jsonObjRecibido.getJSONObject("ciclo").getString("label"));
                }

                if(jsonObjRecibido.has("grupo")){
                    txtGrupoEvaluacion.setText(jsonObjRecibido.getJSONObject("grupo").getString("label"));
                }

                //Llenar spinner de alumno si existe
                if(jsonObjRecibido.has("alumno")){
                    Log.e("--","Entro a llenar el Spinner de Alumno");
                    elementos_spinner_alumno = new ArrayList<>();
                    alumno = new ArrayList<>();
                    for(int i = 0; i < jsonObjRecibido.getJSONObject("alumno").getJSONArray("select").length(); i++){
                        elementos_spinner_alumno.add(jsonObjRecibido.getJSONObject("alumno").getJSONArray("select").getJSONObject(i).getString("nombre"));
                        alumno.add(new Alumno(
                                Integer.parseInt(jsonObjRecibido.getJSONObject("alumno").getJSONArray("select").getJSONObject(i).getString("id")),
                                jsonObjRecibido.getJSONObject("alumno").getJSONArray("select").getJSONObject(i).getString("nombre")));
                    }
                }

                //Llenar spinner de ciclo
                if(jsonObjRecibido.has("ciclo")){
                    Log.e("--","Entro a llenar el Spinner de Ciclo");
                    ciclo  = new ArrayList<>();
                    elementos_spinner_ciclo = new ArrayList<>();
                    for(int i = 0; i < jsonObjRecibido.getJSONObject("ciclo").getJSONArray("select").length(); i++){
                        elementos_spinner_ciclo.add(jsonObjRecibido.getJSONObject("ciclo").getJSONArray("select").getJSONObject(i).getString("nombre"));
                        ciclo.add(new Escuelas(
                                Integer.parseInt(jsonObjRecibido.getJSONObject("ciclo").getJSONArray("select").getJSONObject(i).getString("id")),
                                jsonObjRecibido.getJSONObject("ciclo").getJSONArray("select").getJSONObject(i).getString("nombre"),
                                "0"));
                    }
                }

                //Llenar spinner de grupo
                if(jsonObjRecibido.has("grupo")){
                    Log.e("--","Entro a llenar el Spinner de Grupo");
                    grupo = new ArrayList<>();
                    elementos_spinner_grupo = new ArrayList<>();
                    for(int i = 0; i < jsonObjRecibido.getJSONObject("grupo").getJSONArray("select").length(); i++){
                        elementos_spinner_grupo.add(jsonObjRecibido.getJSONObject("grupo").getJSONArray("select").getJSONObject(i).getString("nombre"));
                        grupo.add(new Periodo(
                                Integer.parseInt(jsonObjRecibido.getJSONObject("grupo").getJSONArray("select").getJSONObject(i).getString("id")),
                                jsonObjRecibido.getJSONObject("grupo").getJSONArray("select").getJSONObject(i).getString("nombre")));
                    }
                }

                //Obtener numero y nombre de Tabs
                if(jsonObjRecibido.has("tabs")){
                    Log.e("--","Entro a llenar las Tabs");
                    modulos = new ArrayList<>();
                    for(int i = 0; i < jsonObjRecibido.getJSONArray("tabs").length(); i++){
                        modulos.add(new Modulo(i,
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getString("titulo"),
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getInt("tipo"),
                                jsonObjRecibido.getJSONArray("tabs").getJSONObject(i).getInt("numero")));
                    }
                }
            } else {
                layAgrupacionEvaluacion.setVisibility(View.GONE);
                txtNoCursosEvaluacion.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Si es un padre de familia, entra al if
        if(tipo_usuario == 6 && jsonObjRecibido.has("alumno")){
            ArrayAdapter<String> dataAdapterAlumno = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, elementos_spinner_alumno);
            spinnerHijosEvaluacion.setAdapter(dataAdapterAlumno);
            layHijosEvaluacion.setVisibility(View.VISIBLE);

            spinnerHijosEvaluacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    codigoAlumnoE = String.valueOf(alumno.get(i).obtenerId());

                    if(check.isConnected(getActivity())){
                        if(!pvAlumno){
                            recargarDatosHijo(codigoAlumnoE);
                        }
                        pvAlumno = false;
                    } else {
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
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });

            codigoAlumnoE = String.valueOf(alumno.get(0).obtenerId());
        }


        //Agregar el listado del spinner al spinner
        if(jsonObjRecibido.has("ciclo")){
            ArrayAdapter<String> dataAdapterCiclo = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, elementos_spinner_ciclo);
            spinnerCicloEvaluacion.setAdapter(dataAdapterCiclo);

            spinnerCicloEvaluacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    cicloE = String.valueOf(ciclo.get(i).obtenerId());

                    if(check.isConnected(getActivity())){
                        if(!pVCiclo){
                            recargarDatosCiclo(cicloE);
                        }
                        pVCiclo = false;
                    } else{
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
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });
        }

        ArrayAdapter<String> dataAdapterGrupo = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, elementos_spinner_grupo);
        spinnerGrupoEvaluacion.setAdapter(dataAdapterGrupo);

        spinnerGrupoEvaluacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                grupoE = String.valueOf(grupo.get(i).obtenerId());
                Log.e("Grupo: ",""+grupoE);
                if(check.isConnected(getActivity())){
                    if(!pvGrupo){
                        for(int j = 0; j<modulos.size(); j++){
                            if(modulos.get(j).getTipo() == 1){
                                if(existeDescripcion){
                                    Tab_Descripcion.setValores(codigoAlumnoE, cicloE, grupoE);
                                }
                            }

                            if(modulos.get(j).getTipo() == 2){
                                if(existeEvaluacion){
                                    Tab_Evaluacion.setValores(codigoAlumnoE, cicloE, grupoE);
                                }

                                if(existeEvaluacionA){
                                    Tab_Evaluacion_a.setValores(codigoAlumnoE, cicloE, grupoE);
                                }
                            }

                            if(modulos.get(j).getTipo() == 3){
                                if(existeModulos){
                                    Tab_Modulos.setValores(codigoAlumnoE, cicloE, grupoE);
                                }
                            }
                            if(modulos.get(j).getTipo() == 4){
                                if(existeCalificacion){
                                    Tab_Calificacion.setValores(codigoAlumnoE, cicloE, grupoE);
                                }
                            }
                        }
                    }
                    pvGrupo = false;
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
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

    }

    public void llenarTabs(JSONObject jsonObjRecibido){
        try {
            //Crear los Tabs
            if(jsonObjRecibido.get("status").equals("success")){
                Adaptador_Tabs_Evaluacion adapter = new Adaptador_Tabs_Evaluacion(frMng, modulos, getActivity(), String.valueOf(ciclo.get(0).obtenerId()), String.valueOf(grupo.get(0).obtenerId()), codigoAlumnoE);
                viewPagerEvaluacion.setAdapter(adapter);
                viewPagerEvaluacion.setOffscreenPageLimit(modulos.size());
                tabsModulosEvaluacion.setupWithViewPager(viewPagerEvaluacion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void recargarDatosCiclo(String codigo_ciclo){
        HashMap<String, String> parametros_recargar_ciclo = new HashMap<>();
        parametros_recargar_ciclo.put("fun","5");
        parametros_recargar_ciclo.put("esc",""+id_escuela);
        if(tipo_usuario == 6){
            parametros_recargar_ciclo.put("codigo",""+codigoAlumnoE);
        } else {
            parametros_recargar_ciclo.put("codigo",""+codigo_usuario);
        }

        parametros_recargar_ciclo.put("tipo",""+tipo_usuario);
        parametros_recargar_ciclo.put("ciclo",""+codigo_ciclo);
        JSONObject jsonObjRecibido = conectar.connect(url_spinner_evaluacion, parametros_recargar_ciclo, getActivity());

        try {
            if(jsonObjRecibido.get("status").equals("fail") || jsonObjRecibido.getJSONObject("grupo").getJSONArray("select").length() == 0){
                //Crear JSON que ficticio que sustituya el vacio
                JSONObject j = new JSONObject();
                j.put("id", "-1");
                j.put("nombre", "El usuario no tiene cursos registrados");

                JSONArray jsa = new JSONArray();
                jsa.put(j);

                JSONObject jo = new JSONObject();
                jo.put("label", "Grupo:");
                jo.put("select", jsa);

                jsonObjRecibido = new JSONObject();
                jsonObjRecibido.put("status", "success");
                jsonObjRecibido.put("grupo", jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pVCiclo = true;
        pvGrupo = false;
        llenarSpinners(jsonObjRecibido);
    }

    public void recargarDatosHijo(String codigo_alumno){
        HashMap<String, String> parametros_recargar_hijo = new HashMap<>();
        parametros_recargar_hijo.put("fun","4");
        parametros_recargar_hijo.put("esc",""+id_escuela);
        parametros_recargar_hijo.put("codigo",""+codigo_alumno);

        JSONObject jsonObjRecibido = conectar.connect(url_spinner_evaluacion, parametros_recargar_hijo, getActivity());
        pVCiclo = true;
        pvGrupo = false;
        llenarSpinners(jsonObjRecibido);
    }
}
