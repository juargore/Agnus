package com.proj.agnus.comun;

import android.os.Parcelable;

/**
 * Created by Arturo on 4/18/2018.
 */

public class Asistencia_Alumno extends Elemento {

    String codigo, status, tot_asis_alum, porcentaje, color;
    String colorm1, color0, color1, textom1, texto0, texto1;
    int estado_nuevo, estado_actual;

    public Asistencia_Alumno(int id, String nombre, String codigo, String status, String tot_asis_alum,
                             String porcentaje, String color, int estado_nuevo, String colorm1, String color0, String color1,
                             String textom1, String texto0, String texto1, int estado_actual) {
        super(id, nombre);

        this.codigo = codigo;
        this.status = status;
        this.tot_asis_alum = tot_asis_alum;
        this.porcentaje = porcentaje;
        this.color = color;
        this.estado_nuevo = estado_nuevo;
        this.colorm1 = colorm1;
        this.color0 = color0;
        this.color1 = color1;

        this.textom1 = textom1;
        this.texto0 = texto0;
        this.texto1 = texto1;
        this.estado_actual = estado_actual;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTot_asis_alum() {
        return tot_asis_alum;
    }

    public void setTot_asis_alum(String tot_asis_alum) {
        this.tot_asis_alum = tot_asis_alum;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getEstado_nuevo() {
        return estado_nuevo;
    }

    public void setEstado_nuevo(int estado_nuevo) {
        this.estado_nuevo = estado_nuevo;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getTexto1() {
        return texto1;
    }

    public void setTexto1(String texto1) {
        this.texto1 = texto1;
    }

    public int getEstado_actual() {
        return estado_actual;
    }

    public void setEstado_actual(int estado_actual) {
        this.estado_actual = estado_actual;
    }

    public String getColorm1() {
        return colorm1;
    }

    public void setColorm1(String colorm1) {
        this.colorm1 = colorm1;
    }

    public String getColor0() {
        return color0;
    }

    public void setColor0(String color0) {
        this.color0 = color0;
    }

    public String getTextom1() {
        return textom1;
    }

    public void setTextom1(String textom1) {
        this.textom1 = textom1;
    }

    public String getTexto0() {
        return texto0;
    }

    public void setTexto0(String texto0) {
        this.texto0 = texto0;
    }
}
