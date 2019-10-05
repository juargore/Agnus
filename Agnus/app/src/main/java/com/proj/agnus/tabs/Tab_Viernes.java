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

public class Tab_Viernes extends Fragment {

    private View rootView;
    private static RecyclerView listaHorariosVie;
    public static Adaptador_Horarios adapter;
    static ArrayList<Horario> lista = new ArrayList<>();
    static SharedPreferences prefs;
    static int tipo_usuario;
    static TextView txtNoHV;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    static Context context;
    static JSONObject jsonVie;

    public Tab_Viernes(Context context, List<String> listaNombres, List<String> listaCodigos, JSONObject jsonVie) {
        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jsonVie = jsonVie;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_tab__viernes, container, false);
        listaHorariosVie = (RecyclerView) rootView.findViewById(R.id.listaHorariosVie);
        txtNoHV = (TextView) rootView.findViewById(R.id.txtNoHV);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaHorariosVie.setLayoutManager(layoutManager);

        llenarListaVie(0);
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
            listaHorariosVie.setAdapter(adapter);
        }
    }

    public static void llenarListaVie(int position){
        Log.e("VIE",""+jsonVie);
        if(jsonVie != null){
            try {
                if(jsonVie.has("Hijo"+position) && jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").length()>0){
                    txtNoHV.setVisibility(View.GONE);
                    listaHorariosVie.setVisibility(View.VISIBLE);
                    lista = new ArrayList<>();
                    lista.clear();
                    for(int i = 0; i<jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").length(); i++){
                        lista.add(new Horario(i, "V",
                                (String) jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).get("materia"),
                                (String) jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).get("hora"),
                                (String) jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).get("grupo"),
                                (String) jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).get("aula"),

                                (jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).has("profesor")?
                                        (String) jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).get("profesor"):
                                        (String) jsonVie.getJSONObject("Hijo"+position).getJSONArray("V").getJSONObject(i).get("clave"))));
                    }
                } else {
                    txtNoHV.setVisibility(View.VISIBLE);
                    listaHorariosVie.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adaptadorLleno();
        }
    }
}
