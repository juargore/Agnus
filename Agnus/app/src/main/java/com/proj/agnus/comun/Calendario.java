package com.proj.agnus.comun;

/**
 * Created by Arturo on 4/16/2018.
 */

public class Calendario extends Elemento {

    private String descripcion, producto, entrega, recibe, tipo, fecha_ini, hora_ini, fecha_fin, hora_fin;

    public Calendario(int id, String titulo, String descripcion, String producto, String entrega, String recibe,
                      String tipo, String fecha_ini, String hora_ini, String fecha_fin, String hora_fin) {
        super(id, titulo);

        this.descripcion = descripcion;
        this.producto = producto;
        this.entrega = entrega;
        this.recibe = recibe;
        this.tipo = tipo;
        this.fecha_ini = fecha_ini;
        this.hora_ini = hora_ini;
        this.fecha_fin = fecha_fin;
        this.hora_fin = hora_fin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getEntrega() {
        return entrega;
    }

    public void setEntrega(String entrega) {
        this.entrega = entrega;
    }

    public String getRecibe() {
        return recibe;
    }

    public void setRecibe(String recibe) {
        this.recibe = recibe;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha_ini() {
        return fecha_ini;
    }

    public void setFecha_ini(String fecha_ini) {
        this.fecha_ini = fecha_ini;
    }

    public String getHora_ini() {
        return hora_ini;
    }

    public void setHora_ini(String hora_ini) {
        this.hora_ini = hora_ini;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }
}
