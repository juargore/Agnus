package com.proj.agnus.comun;

/**
 * Created by Arturo on 3/22/2018.
 */

public class Email extends Elemento {

    private String img;
    private String leido;
    private String De;
    private String Fecha;
    private String Mensaje;


    //nombre = Asunto
    public Email(int id, String nombre, String img, String leido, String De, String Fecha, String Mensaje) {
        super(id, nombre);

        this.img = img;
        this.leido = leido;
        this.De = De;
        this.Fecha = Fecha;
        this.Mensaje = Mensaje;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLeido() {
        return leido;
    }

    public void setLeido(String leido) {
        this.leido = leido;
    }

    public String getDe() {
        return De;
    }

    public void setDe(String de) {
        De = de;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }
}
