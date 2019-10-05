package com.proj.agnus.comun;

/**
 * Created by Arturo on 5/30/2018.
 */

public class Alumno_Evaluacion extends Elemento {

    String area, cal, color, status;

    public Alumno_Evaluacion(int id, String nombre, String area, String cal, String color, String status) {
        super(id, nombre);

        this.area = area;
        this.cal = cal;
        this.color = color;
        this.status = status;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
