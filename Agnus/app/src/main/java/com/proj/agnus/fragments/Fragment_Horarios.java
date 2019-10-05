package com.proj.agnus.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
import com.proj.agnus.adaptadores.Adaptador_Horarios;
import com.proj.agnus.adaptadores.Adaptador_Tabs_Horarios;
import com.proj.agnus.comun.Alumno;
import com.proj.agnus.comun.Horario;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.proj.agnus.tabs.Tab_Jueves.llenarListaJue;
import static com.proj.agnus.tabs.Tab_Lunes.llenarListaLun;
import static com.proj.agnus.tabs.Tab_Martes.llenarListaMar;
import static com.proj.agnus.tabs.Tab_Miercoles.llenarListaMie;
import static com.proj.agnus.tabs.Tab_Sabado.llenarListaSab;
import static com.proj.agnus.tabs.Tab_Viernes.llenarListaVie;

@SuppressLint("ValidFragment")
public class Fragment_Horarios extends Fragment {

    SharedPreferences prefs;
    int tipo_usuario, codigo_usuario, id_escuela;
    TabLayout tabsHorario;
    ViewPager viewPager;
    FragmentManager fra1;
    Spinner spinnerHijos;
    RelativeLayout rLSpinner;
    EditText etNombreCiclo;
    TextView txtNombreCiclo;
    ImageView ivFlechitaHijos;

    private Context context;
    static Adaptador_Horarios adapter;
    private ArrayList<Alumno> listaHijos;
    private ArrayList<Horario> listaHorario;
    public connect_HttpPost conectar;
    private JSONObject jsonObjRecibido;
    String url_pantalla_tabs = "agnus.mx/app/horario.php";

    public Fragment_Horarios(FragmentManager fra, Context context){
        fra1 = fra;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_horarios, container, false);

        tabsHorario = (TabLayout) rootView.findViewById(R.id.tabsHorario);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        spinnerHijos = (Spinner) rootView.findViewById(R.id.spinnerHijos);
        etNombreCiclo = (EditText) rootView.findViewById(R.id.etNombreCiclo);
        txtNombreCiclo = (TextView) rootView.findViewById(R.id.txtAlumno_Uac_Asis);
        rLSpinner = (RelativeLayout) rootView.findViewById(R.id.rLSpinner);
        ivFlechitaHijos = (ImageView) rootView.findViewById(R.id.ivFlechitaHijos);

        conectar = new connect_HttpPost();
        listaHijos = new ArrayList<>();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));
        codigo_usuario = Integer.parseInt(prefs.getString("codigo_usuario", "1"));
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));

        List<String> alumnos_lista = new ArrayList<>();
        HashMap<String, String> parametros_escuelas = new HashMap<>();
        parametros_escuelas.put("fun","1");
        parametros_escuelas.put("esc",""+id_escuela);
        parametros_escuelas.put("tipo",""+tipo_usuario);
        parametros_escuelas.put("codigo",""+codigo_usuario);
        jsonObjRecibido = conectar.connect(url_pantalla_tabs, parametros_escuelas, getActivity());

        JSONObject joL, joM, joI, joJ, joV, joS;
        JSONObject jaL = new JSONObject();
        JSONObject jaM = new JSONObject();
        JSONObject jaI = new JSONObject();
        JSONObject jaJ = new JSONObject();
        JSONObject jaV = new JSONObject();
        JSONObject jaS = new JSONObject();

        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("horarios").length(); i++ ){
                joL = new JSONObject();
                joL.put("L",jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).getJSONObject("horario").get("L"));
                jaL.accumulate("Hijo"+i, joL);

                joM = new JSONObject();
                joM.put("M",jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).getJSONObject("horario").get("M"));
                jaM.accumulate("Hijo"+i, joM);

                joI = new JSONObject();
                joI.put("I",jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).getJSONObject("horario").get("I"));
                jaI.accumulate("Hijo"+i, joI);

                joJ = new JSONObject();
                joJ.put("J",jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).getJSONObject("horario").get("J"));
                jaJ.accumulate("Hijo"+i, joJ);

                joV = new JSONObject();
                joV.put("V",jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).getJSONObject("horario").get("V"));
                jaV.accumulate("Hijo"+i, joV);

                joS = new JSONObject();
                joS.put("S",jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).getJSONObject("horario").get("S"));
                jaS.accumulate("Hijo"+i, joS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<String> listaNombres = new ArrayList<>();
        List<String> listaCodigos = new ArrayList<>();

        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("horarios").length(); i++ ){
                if(jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).has("codigo"))
                        listaCodigos.add(i, ""+jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).get("codigo"));

                if(jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).has("nombre"))
                    listaNombres.add(i, ""+jsonObjRecibido.getJSONArray("horarios").getJSONObject(i).get("nombre"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Adaptador_Tabs_Horarios adapter = new Adaptador_Tabs_Horarios(getActivity(), fra1, listaNombres, listaCodigos, jaL, jaM, jaI, jaJ, jaV, jaS);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(6);
        tabsHorario.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabsHorario.getTabAt(diaSemana());
        tab.select();


        if(tipo_usuario == 6){ //Padre de familia
            txtNombreCiclo.setText("Nombre del alumno");
            etNombreCiclo.setVisibility(View.GONE);
            rLSpinner.setVisibility(View.VISIBLE);

            for(int i=0; i<listaNombres.size(); i++){
                alumnos_lista.add(""+listaNombres.get(i));
            }

            //Propiedades y asignacion al Spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, alumnos_lista);
            spinnerHijos.setAdapter(dataAdapter);
            spinnerHijos.setSelection(0, true);

            if(alumnos_lista.size()<2){
                ivFlechitaHijos.setVisibility(View.GONE);
            }


        } else {
            txtNombreCiclo.setText("Ciclo escolar");
            try {
                etNombreCiclo.setText(""+jsonObjRecibido.get("ciclo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            etNombreCiclo.setVisibility(View.VISIBLE);
            rLSpinner.setVisibility(View.GONE);
        }

        spinnerHijos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#0D3863"));
                ((TextView) adapterView.getChildAt(0)).setTextSize(12);

                check_internet_connection check = new check_internet_connection();
                if(check.isConnected(getActivity())){
                    llenarListaLun(i); llenarListaMar(i); llenarListaMie(i);
                    llenarListaJue(i); llenarListaVie(i); llenarListaSab(i);
                }else {
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

        return rootView;
    }

    public int diaSemana(){
        Calendar myDate = Calendar.getInstance();
        int diaSemana = myDate.get (Calendar.DAY_OF_WEEK);

        switch (diaSemana){
            case 1: /*Dom*/ return 0;
            case 2: /*Lun*/ return 0;
            case 3: /*Mar*/ return 1;
            case 4: /*Mie*/ return 2;
            case 5: /*Jue*/ return 3;
            case 6: /*Vie*/ return 4;
            case 7: /*Sab*/ return 5;
            default: return 0;
        }
    }
}
