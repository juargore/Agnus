package com.proj.agnus.comun;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

//import com.glass.agnus.servicios.ExpandableGroup;

/**
 * Created by Arturo on 6/6/2018.
 */

public class Alumno_Foro extends ExpandableGroup<Comentario_Foro> {

    public Alumno_Foro(String title, List<Comentario_Foro> items) {
        super(title, items);
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public List<Comentario_Foro> getItems() {
        return super.getItems();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
