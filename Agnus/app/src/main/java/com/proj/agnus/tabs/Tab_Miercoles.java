package com.proj.agnus.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Horarios;
import com.proj.agnus.comun.Horario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Tab_Miercoles extends Fragment {


    private View rootView;
    private static RecyclerView listaHorariosMie;
    public static Adaptador_Horarios adapter;
    static ArrayList<Horario> lista = new ArrayList<>();
    static SharedPreferences prefs;
    static int tipo_usuario;
    static TextView txtNoHI;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    static Context context;
    static JSONObject jsonMie;

    public Tab_Miercoles(Context context, List<String> listaNombres, List<String> listaCodigos, JSONObject jsonMie) {
        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jsonMie = jsonMie;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab__miercoles, container, false);

        listaHorariosMie = (RecyclerView) rootView.findViewById(R.id.listaHorariosMie);
        txtNoHI = (TextView) rootView.findViewById(R.id.txtNoHI);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaHorariosMie.setLayoutManager(layoutManager);
        
        llenarListaMie(0);

        return rootView;
    }

    public static void adaptadorLleno(){
        prefs = context.getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));

        if(lista.size() > 0){
            adapter = new Adaptador_Horarios(lista, context, new Adaptador_Horarios.OnItemClickListener() {
                @Override
                public void OnItemClick(Horario horario) {

                }
            }, tipo_usuario);
            listaHorariosMie.setAdapter(adapter);
        }
    }

    public static void llenarListaMie(int position){
        Log.e("MIE",""+jsonMie);
        if(jsonMie != null){
            try {
                if(jsonMie.has("Hijo"+position) && jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").length()>0){
                    txtNoHI.setVisibility(View.GONE);
                    listaHorariosMie.setVisibility(View.VISIBLE);
                    lista = new ArrayList<>();
                    lista.clear();
                    for(int i = 0; i<jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").length(); i++){
                        lista.add(new Horario(i, "I",
                                (String) jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).get("materia"),
                                (String) jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).get("hora"),
                                (String) jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).get("grupo"),
                                (String) jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).get("aula"),

                                (jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).has("profesor")?
                                        (String) jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).get("profesor"):
                                        (String) jsonMie.getJSONObject("Hijo"+position).getJSONArray("I").getJSONObject(i).get("clave"))));
                    }
                } else {
                    txtNoHI.setVisibility(View.VISIBLE);
                    listaHorariosMie.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adaptadorLleno();
        }
    }
}
