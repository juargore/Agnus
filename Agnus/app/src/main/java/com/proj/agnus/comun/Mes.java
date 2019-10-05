package com.proj.agnus.comun;

/**
 * Created by Arturo on 4/18/2018.
 */

public class Mes extends Elemento {

    String id_mes;

    public Mes(int id, String id_mes, String nombre  ) {
        super(id, nombre);

        this.id_mes = id_mes;
    }

    public String getId_mes() {
        return id_mes;
    }

    public void setId_mes(String id_mes) {
        this.id_mes = id_mes;
    }
}
