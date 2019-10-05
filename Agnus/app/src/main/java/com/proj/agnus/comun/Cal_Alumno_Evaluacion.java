package com.proj.agnus.comun;

/**
 * Created by Arturo on 5/30/2018.
 */

public class Cal_Alumno_Evaluacion extends Elemento {

    String cal, color;

    public Cal_Alumno_Evaluacion(int id, String nombre, String cal, String color) {
        super(id, nombre);

        this.cal = cal;
        this.color = color;
    }

    public String getCal() {
        return cal;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
