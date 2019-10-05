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

public class Tab_Martes extends Fragment {

    private View rootView;
    private static RecyclerView listaHorariosMar;
    public static Adaptador_Horarios adapter;
    static ArrayList<Horario> lista = new ArrayList<>();
    static SharedPreferences prefs;
    static int tipo_usuario;
    static TextView txtNoHM;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    static Context context;
    static JSONObject jsonMartes;

    public Tab_Martes(Context context, List<String> listaNombres, List<String> listaCodigos, JSONObject jsonMartes) {
        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jsonMartes = jsonMartes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab__martes, container, false);
        listaHorariosMar = (RecyclerView) rootView.findViewById(R.id.listaHorariosMar);
        txtNoHM = (TextView) rootView.findViewById(R.id.txtNoHM);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaHorariosMar.setLayoutManager(layoutManager);

        llenarListaMar(0);
        
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
            listaHorariosMar.setAdapter(adapter);
        }
    }

    public static void llenarListaMar(int position){
        Log.e("MAR",""+jsonMartes);
        if(jsonMartes != null){
            try {
                if(jsonMartes.has("Hijo"+position) && jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").length()>0){
                    txtNoHM.setVisibility(View.GONE);
                    listaHorariosMar.setVisibility(View.VISIBLE);
                    lista = new ArrayList<>();
                    lista.clear();
                    for(int i = 0; i<jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").length(); i++){
                        lista.add(new Horario(i, "M",
                                (String) jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).get("materia"),
                                (String) jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).get("hora"),
                                (String) jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).get("grupo"),
                                (String) jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).get("aula"),

                                (jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).has("profesor")?
                                        (String) jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).get("profesor"):
                                        (String) jsonMartes.getJSONObject("Hijo"+position).getJSONArray("M").getJSONObject(i).get("clave"))));
                    }
                } else {
                    txtNoHM.setVisibility(View.VISIBLE);
                    listaHorariosMar.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adaptadorLleno();
        }
    }
}
