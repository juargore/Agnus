package com.proj.agnus.adaptadores;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.proj.agnus.comun.Mes;
import com.proj.agnus.fragments.Fragment_Mes_Asistencias;

import java.util.ArrayList;

/**
 * Created by Arturo on 2/23/2018.
 */

public class Adaptador_Tabs_Asistencias extends FragmentStatePagerAdapter {

    Context context;
    Fragment fragment = null;
    ArrayList<Mes> meses;
    int id_uac;

    public Adaptador_Tabs_Asistencias(Context context, FragmentManager fm, ArrayList<Mes> meses, int id_uac) {
        super(fm);
        this.context = context;
        this.meses = meses;
        this.id_uac = id_uac;
    }

    @Override
    public Fragment getItem(int position) {
        fragment = Fragment_Mes_Asistencias.nuevaInstancia(meses.get(position).getId_mes(), id_uac);

        return fragment;
    }

    @Override
    public int getCount() {
        return meses.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String nombre_mes = "";
        for(int i=0; i<meses.size(); i++){
            if(position == i){
                nombre_mes =  meses.get(i).obtenerNombre();
            }
        }
        return nombre_mes;
    }
}
