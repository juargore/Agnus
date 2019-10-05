package com.proj.agnus.comun;

/**
 * Created by Arturo on 5/22/2018.
 */

public class Modulo extends Elemento {

    int tipo, numero;

    public Modulo(int id, String titulo, int tipo, int numero) {
        super(id, titulo);

        this.tipo = tipo;
        this.numero = numero;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
