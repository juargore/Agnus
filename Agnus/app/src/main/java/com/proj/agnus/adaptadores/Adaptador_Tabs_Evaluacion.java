package com.proj.agnus.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.proj.agnus.comun.Modulo;
import com.proj.agnus.fragments.Fragment_Evaluacion;
import com.proj.agnus.tabs.Tab_Calificacion;
import com.proj.agnus.tabs.Tab_Descripcion;
import com.proj.agnus.tabs.Tab_Evaluacion;
import com.proj.agnus.tabs.Tab_Evaluacion_a;
import com.proj.agnus.tabs.Tab_Modulos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arturo on 2/23/2018.
 */

public class Adaptador_Tabs_Evaluacion extends FragmentStatePagerAdapter {

    private List<Modulo> modulos;
    private Fragment fragment = null;
    private Context context;
    private String ciclo, grupo, codigoAlumno;

    public Adaptador_Tabs_Evaluacion(FragmentManager fm, ArrayList<Modulo> modulos,
                                     Activity activity, String ciclo, String grupo, String codigoAlumno) {
        super(fm);

        this.modulos = modulos;
        this.context = activity;
        this.ciclo = ciclo;
        this.grupo = grupo;
        this.codigoAlumno = codigoAlumno;
    }

    @Override
    public Fragment getItem(int position) {

        switch (modulos.get(position).getTipo()){
            case 1:
                Fragment_Evaluacion.existeDescripcion = true;
                Tab_Descripcion tDesc = new Tab_Descripcion(ciclo, grupo, codigoAlumno);
                return tDesc;
            case 2:
                if(modulos.get(position).getNumero() == 1 ||
                        modulos.get(position).getNumero() == 2){
                    Fragment_Evaluacion.existeEvaluacion = true;
                    Tab_Evaluacion tEval = new Tab_Evaluacion(ciclo, grupo, codigoAlumno);
                    Bundle args = new Bundle();
                    args.putSerializable("numero", ""+modulos.get(position).getNumero());
                    tEval.setArguments(args);

                    return tEval;
                } else {
                    Fragment_Evaluacion.existeEvaluacionA = true;
                    Tab_Evaluacion_a tEval = new Tab_Evaluacion_a(ciclo, grupo, codigoAlumno);
                    Bundle args = new Bundle();
                    args.putSerializable("numero", ""+modulos.get(position).getNumero());
                    tEval.setArguments(args);

                    return tEval;
                }
            case 3:
                Fragment_Evaluacion.existeModulos = true;
                Tab_Modulos tMod = new Tab_Modulos(ciclo, grupo, codigoAlumno);
                return tMod;
            case 4:
                Fragment_Evaluacion.existeCalificacion = true;
                Tab_Calificacion tCal = new Tab_Calificacion(4, ciclo, grupo, codigoAlumno);
                return tCal;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return modulos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String nombre_mes = "";
        for(int i=0; i<modulos.size(); i++){
            if(position == i){
                nombre_mes =  modulos.get(i).obtenerNombre();
            }
        }
        return nombre_mes;
    }
}
