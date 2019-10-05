package com.proj.agnus.comun;

/**
 * Created by Arturo on 6/5/2018.
 */

public class Recursos extends Elemento {

    String descripcion, tipo, enlace, label;

    public Recursos(int id, String titulo, String descripcion, String tipo, String enlace, String label) {
        super(id, titulo);

        this.descripcion = descripcion;
        this.tipo = tipo;
        this.enlace = enlace;
        this.label = label;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
