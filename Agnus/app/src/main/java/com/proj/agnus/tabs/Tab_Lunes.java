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

public class Tab_Lunes extends Fragment {

    private View rootView;
    private static RecyclerView listaHorariosLun;
    public static Adaptador_Horarios adapter;
    static ArrayList<Horario> lista = new ArrayList<>();
    static SharedPreferences prefs;
    static int tipo_usuario;
    static TextView txtNoHL;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    static Context context;
    static JSONObject jsonLunes;

    public Tab_Lunes(Context context, List<String> listaNombres, List<String> listaCodigos, JSONObject jsonLunes) {
        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jsonLunes = jsonLunes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_lunes, container, false);
        listaHorariosLun = (RecyclerView) rootView.findViewById(R.id.listaHorariosLun);
        txtNoHL = (TextView) rootView.findViewById(R.id.txtNoHL);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listaHorariosLun.setLayoutManager(layoutManager);

        llenarListaLun(0);

        return  rootView;
    }

    public static void adaptadorLleno(){
        if(lista.size() > 0){
            adapter = new Adaptador_Horarios(lista, context, new Adaptador_Horarios.OnItemClickListener() {
                @Override
                public void OnItemClick(Horario horario) {
                    int position = horario.obtenerId();
                }
            }, tipo_usuario);
            listaHorariosLun.setAdapter(adapter);
        }
    }

    public static void llenarListaLun(int position){
        prefs = context.getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        tipo_usuario = Integer.parseInt(prefs.getString("tipo_usuario", "1"));

        Log.e("LUN",""+jsonLunes);
        if(jsonLunes != null){
            try {
                if(jsonLunes.has("Hijo"+position) && jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").length()>0){
                    txtNoHL.setVisibility(View.GONE);
                    listaHorariosLun.setVisibility(View.VISIBLE);
                    lista = new ArrayList<>();
                    lista.clear();
                    for(int i = 0; i<jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").length(); i++){
                        lista.add(new Horario(i, "L",
                                (String) jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).get("materia"),
                                (String) jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).get("hora"),
                                (String) jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).get("grupo"),
                                (String) jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).get("aula"),

                                (jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).has("profesor")?
                                        (String) jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).get("profesor"):
                                        (String) jsonLunes.getJSONObject("Hijo"+position).getJSONArray("L").getJSONObject(i).get("clave"))));
                    }
                } else {
                    txtNoHL.setVisibility(View.VISIBLE);
                    listaHorariosLun.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adaptadorLleno();
        }

    }
}
