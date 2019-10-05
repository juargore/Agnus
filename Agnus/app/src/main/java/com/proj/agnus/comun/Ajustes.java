package com.proj.agnus.comun;

/**
 * Created by Arturo on 6/20/2018.
 */

public class Ajustes extends Elemento {

    private String icono, subtitulo;

    public Ajustes(int id, String nombre, String icono, String subtitulo) {
        super(id, nombre);

        this.icono = icono;
        this.subtitulo = subtitulo;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }
}
