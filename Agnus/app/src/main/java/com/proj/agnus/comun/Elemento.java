package com.proj.agnus.comun;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arturo on 2/10/18.
 */

public class Elemento implements Parcelable {

    private int id;
    private String nombre;

    public Elemento(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public static final Creator<Elemento> CREATOR = new Creator<Elemento>() {
        @Override
        public Elemento createFromParcel(Parcel in) {
            return new Elemento(in);
        }

        @Override
        public Elemento[] newArray(int size) {
            return new Elemento[size];
        }
    };

    public Elemento(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
    }


    public int obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return obtenerNombre();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre);
    }
}
