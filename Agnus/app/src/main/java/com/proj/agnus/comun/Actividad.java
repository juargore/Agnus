package com.proj.agnus.comun;

/**
 * Created by Arturo on 6/7/2018.
 */

public class Actividad extends Elemento {

    private String cal, color, archivo, link, extension, status;

    public Actividad(int id, String nombre, String cal, String color, String archivo, String link, String extension, String status) {
        super(id, nombre);

        this.cal = cal;
        this.color = color;
        this.archivo = archivo;
        this.link = link;
        this.extension = extension;
        this.status = status;
    }

    public String getCal() {
        return cal;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
