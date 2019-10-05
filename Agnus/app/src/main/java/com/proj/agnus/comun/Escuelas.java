package com.proj.agnus.comun;

/**
 * Created by Arturo on 2/10/2018.
 */

public class Escuelas extends Elemento {

    private String tipo_usuario;

    public Escuelas(int id, String nombre, String tipo_usuario) {
        super(id, nombre);
        this.tipo_usuario = tipo_usuario;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }
}
