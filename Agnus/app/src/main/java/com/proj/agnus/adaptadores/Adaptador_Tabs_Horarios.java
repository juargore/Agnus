package com.proj.agnus.adaptadores;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.proj.agnus.tabs.Tab_Jueves;
import com.proj.agnus.tabs.Tab_Lunes;
import com.proj.agnus.tabs.Tab_Martes;
import com.proj.agnus.tabs.Tab_Miercoles;
import com.proj.agnus.tabs.Tab_Sabado;
import com.proj.agnus.tabs.Tab_Viernes;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arturo on 2/23/2018.
 */

public class Adaptador_Tabs_Horarios extends FragmentStatePagerAdapter {

    Context context;
    List<String> listaNombres = new ArrayList<>();
    List<String> listaCodigos = new ArrayList<>();
    JSONObject jaL, jaM, jaI, jaJ, jaV, jaS;

    public Adaptador_Tabs_Horarios(Context context, FragmentManager fm, List<String> listaNombres, List<String> listaCodigos, JSONObject jaL, JSONObject jaM, JSONObject jaI, JSONObject jaJ, JSONObject jaV, JSONObject jaS) {
        super(fm);

        this.context = context;
        this.listaNombres = listaNombres;
        this.listaCodigos = listaCodigos;
        this.jaL = jaL;
        this.jaM = jaM;
        this.jaI = jaI;
        this.jaJ = jaJ;
        this.jaV = jaV;
        this.jaS = jaS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Tab_Lunes tlun = new Tab_Lunes(context, listaNombres, listaCodigos, jaL);
                return tlun;
            case 1:
                Tab_Martes tmar = new Tab_Martes(context, listaNombres, listaCodigos, jaM);
                return tmar;
            case 2:
                Tab_Miercoles tmie = new Tab_Miercoles(context, listaNombres, listaCodigos, jaI);
                return tmie;
            case 3:
                Tab_Jueves tjue = new Tab_Jueves(context, listaNombres, listaCodigos, jaJ);
                return tjue;
            case 4:
                Tab_Viernes tvie = new Tab_Viernes(context, listaNombres, listaCodigos, jaV);
                return tvie;
            case 5:
                Tab_Sabado tsab = new Tab_Sabado(context, listaNombres, listaCodigos, jaS);
                return tsab;
            default:
                Tab_Sabado tsab1 = new Tab_Sabado(context, listaNombres, listaCodigos, jaS);
                return tsab1;
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "L u n e s";
            case 1:
                return "M a r t e s";
            case 2:
                return "M i é r c o l e s";
            case 3:
                return "J u e v e s";
            case 4:
                return "V i e r n e s";
            case 5:
                return "S á b a d o";
            default:
                return "L u n e s";
        }
    }
}
