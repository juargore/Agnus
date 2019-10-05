package com.proj.agnus.comun;

/**
 * Created by Arturo on 2/21/2018.
 */

public class Noticias extends Elemento {
    private String descripcion;
    private String fecha;
    private String imagen;

    public Noticias(int id, String nombre, String descripcion, String fecha, String imagen) {
        super(id, nombre);

        this.descripcion = descripcion;
        this.fecha = fecha;
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
