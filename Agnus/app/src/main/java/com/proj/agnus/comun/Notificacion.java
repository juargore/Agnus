package com.proj.agnus.comun;

/**
 * Created by Arturo on 5/8/2018.
 */

public class Notificacion extends Elemento {

    private String icono, fecha, hora, area, leido, mensaje;

    public Notificacion(int id, String cuerpo, String icono, String fecha, String hora, String area, String leido, String mensaje) {
        super(id, cuerpo);

        this.icono = icono;
        this.fecha = fecha;
        this.hora = hora;
        this.area = area;
        this.leido = leido;
        this.mensaje = mensaje;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLeido() {
        return leido;
    }

    public void setLeido(String leido) {
        this.leido = leido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
