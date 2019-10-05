package com.proj.agnus.comun;

/**
 * Created by Arturo on 6/20/2018.
 */

public class Ajustes_Detalles extends Elemento {

    private int valor;

    public Ajustes_Detalles(int id, String nombre, int valor) {
        super(id, nombre);

        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
