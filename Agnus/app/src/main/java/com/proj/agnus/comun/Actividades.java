package com.proj.agnus.comun;

/**
 * Created by Arturo on 4/18/2018.
 */

public class Actividades extends Elemento {

    String tipo, label;

    public Actividades(int id, String titulo, String tipo, String label) {
        super(id, titulo);
        this.tipo = tipo;
        this.label = label;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
