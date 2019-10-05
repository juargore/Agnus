package com.proj.agnus.comun;

/**
 * Created by Arturo on 4/19/2018.
 */

public class Asistencia_Alumno_Detalle extends Elemento {

    int id_asis, cant_asis, estado_nuevo, estado_actual, tipo;
    String fecha, color;
    String color0, color1, colorm1, texto0, texto1, textom1;

    //Fecha, color, etiqueta
    public Asistencia_Alumno_Detalle(int id, String etiqueta, String color, String fecha, String colorm1, String color0,
                                     String color1, String textom1, String texto0, String texto1,
                                     int id_asis, int cant_asis, int estado_nuevo, int estado_actual, int tipo) {
        super(id, etiqueta);
        this.color = color;
        this.fecha = fecha;
        this.color0 = color0;
        this.color1 = color1;
        this.colorm1 = colorm1;
        this.texto0 = texto0;
        this.texto1 = texto1;
        this.textom1 = textom1;
        this.id_asis = id_asis;
        this.cant_asis = cant_asis;
        this.estado_nuevo = estado_nuevo;
        this.estado_actual = estado_actual;
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor0() {
        return color0;
    }

    public void setColor0(String color0) {
        this.color0 = color0;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColorm1() {
        return colorm1;
    }

    public void setColorm1(String colorm1) {
        this.colorm1 = colorm1;
    }

    public String getTexto0() {
        return texto0;
    }

    public void setTexto0(String texto0) {
        this.texto0 = texto0;
    }

    public String getTexto1() {
        return texto1;
    }

    public void setTexto1(String texto1) {
        this.texto1 = texto1;
    }

    public String getTextom1() {
        return textom1;
    }

    public void setTextom1(String textom1) {
        this.textom1 = textom1;
    }

    public int getId_asis() {
        return id_asis;
    }

    public void setId_asis(int id_asis) {
        this.id_asis = id_asis;
    }

    public int getCant_asis() {
        return cant_asis;
    }

    public void setCant_asis(int cant_asis) {
        this.cant_asis = cant_asis;
    }

    public int getEstado_nuevo() {
        return estado_nuevo;
    }

    public void setEstado_nuevo(int estado_nuevo) {
        this.estado_nuevo = estado_nuevo;
    }

    public int getEstado_actual() {
        return estado_actual;
    }

    public void setEstado_actual(int estado_actual) {
        this.estado_actual = estado_actual;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
