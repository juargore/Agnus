package com.proj.agnus.comun;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arturo on 6/6/2018.
 */

public class Comentario_Foro implements Parcelable {

    private int id;
    private String nombre, fecha, texto;

    public Comentario_Foro(int id, String nombre, String fecha, String texto){

        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

}
