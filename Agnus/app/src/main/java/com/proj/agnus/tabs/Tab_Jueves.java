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

public class Tab_Jueves extends Fragment {

    private View rootView;
    private static RecyclerView listaHorariosJue;
    public static Adaptador_Horarios adapter;
    static ArrayList<Horario> lista = new ArrayList<>();
    static SharedPreferences prefs;
    static int tipo_usuario;
    static TextView txtNoHJ;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    static Context context;
    static JSONObject jsonJue;

    public Tab_Jueves(Context context, List<String> listaNombres, List<String> listaCodigos, JSONObject jsonJue) {
        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jsonJue = jsonJue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_tab__jueves, container, false);
        listaHorariosJue = (RecyclerView) rootView.findViewById(R.id.listaHorariosJue);
        txtNoHJ = (TextView) rootView.findViewById(R.id.txtNoHJ);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaHorariosJue.setLayoutManager(layoutManager);

        llenarListaJue(0);
        
        return rootView;
    }

    public static  void adaptadorLleno(){
        prefs = context.getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));

        if(lista.size() > 0){
            adapter = new Adaptador_Horarios(lista, context, new Adaptador_Horarios.OnItemClickListener() {
                @Override
                public void OnItemClick(Horario horario) {

                }
            }, tipo_usuario);
            listaHorariosJue.setAdapter(adapter);
        }
    }

    public static void llenarListaJue(int position){
        Log.e("JUE",""+jsonJue);
        if(jsonJue != null){
            try {
                if(jsonJue.has("Hijo"+position) && jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").length()>0){
                    txtNoHJ.setVisibility(View.GONE);
                    listaHorariosJue.setVisibility(View.VISIBLE);
                    lista = new ArrayList<>();
                    lista.clear();
                    for(int i = 0; i<jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").length(); i++){
                        lista.add(new Horario(i, "J",
                                (String) jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).get("materia"),
                                (String) jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).get("hora"),
                                (String) jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).get("grupo"),
                                (String) jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).get("aula"),

                                (jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).has("profesor")?
                                        (String) jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).get("profesor"):
                                        (String) jsonJue.getJSONObject("Hijo"+position).getJSONArray("J").getJSONObject(i).get("clave"))));
                    }
                } else {
                    txtNoHJ.setVisibility(View.VISIBLE);
                    listaHorariosJue.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adaptadorLleno();
        }
    }
}
