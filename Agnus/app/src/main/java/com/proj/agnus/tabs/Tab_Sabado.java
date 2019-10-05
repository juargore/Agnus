package com.proj.agnus.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class Tab_Sabado extends Fragment {

    private View rootView;
    private static RecyclerView listaHorariosSab;
    public static Adaptador_Horarios adapter;
    static ArrayList<Horario> lista = new ArrayList<>();
    static SharedPreferences prefs;
    static int tipo_usuario;
    static TextView txtNoHS;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    static Context context;
    static JSONObject jsonSab;

    public Tab_Sabado(Context context, List<String> listaNombres, List<String> listaCodigos, JSONObject jsonSab) {
        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jsonSab = jsonSab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab__sabado, container, false);
        listaHorariosSab = (RecyclerView) rootView.findViewById(R.id.listaHorariosSab);
        txtNoHS = (TextView) rootView.findViewById(R.id.txtNoHS);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaHorariosSab.setLayoutManager(layoutManager);

        llenarListaSab(0);
        
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
            listaHorariosSab.setAdapter(adapter);
        }
    }

    public static void llenarListaSab(int position){
        //Log.e("SAB",""+jsonSab);
        if(jsonSab != null){
            try {
                if(jsonSab.has("Hijo"+position) && jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").length()>0){
                    txtNoHS.setVisibility(View.GONE);
                    listaHorariosSab.setVisibility(View.VISIBLE);
                    lista = new ArrayList<>();
                    lista.clear();
                    for(int i = 0; i<jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").length(); i++){
                        lista.add(new Horario(i, "S",
                                (String) jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).get("materia"),
                                (String) jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).get("hora"),
                                (String) jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).get("grupo"),
                                (String) jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).get("aula"),

                                (jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).has("profesor")?
                                        (String) jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).get("profesor"):
                                        (String) jsonSab.getJSONObject("Hijo"+position).getJSONArray("S").getJSONObject(i).get("clave"))));
                    }
                } else {
                    txtNoHS.setVisibility(View.VISIBLE);
                    listaHorariosSab.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adaptadorLleno();
        }
    }
}
