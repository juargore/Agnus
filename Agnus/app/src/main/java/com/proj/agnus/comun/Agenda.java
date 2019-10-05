package com.proj.agnus.comun;

/**
 * Created by Arturo on 3/15/2018.
 */

public class Agenda extends Elemento {

    private String descripcion, lugar, fecha_ini, hora_ini, fecha_fin, hora_fin, responsable, departamento, grupo;

    public Agenda(int id, String titulo, String descripcion, String lugar, String fecha_ini, String hora_ini,
                  String fecha_fin, String hora_fin, String responsable, String departamento, String grupo) {
        super(id, titulo);

        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fecha_ini = fecha_ini;
        this.hora_ini = hora_ini;
        this.fecha_fin = fecha_fin;
        this.hora_fin = hora_fin;
        this.responsable = responsable;
        this.departamento = departamento;
        this.grupo = grupo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
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

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}

