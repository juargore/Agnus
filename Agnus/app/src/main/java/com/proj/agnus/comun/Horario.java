package com.proj.agnus.comun;

/**
 * Created by Arturo on 2/26/2018.
 */

public class Horario extends Elemento {

    private String hora, grupo, salon, profesor_clave;

    public Horario(int id, String dia, String nombre_materia, String hora,
                   String grupo, String salon, String profesor_clave) {
        super(id, nombre_materia);

        this.hora = hora;
        this.grupo = grupo;
        this.salon = salon;
        this.profesor_clave = profesor_clave;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getProfesorClave() {
        return profesor_clave;
    }

    public void setProfesorClave(String profesor_clave) {
        this.profesor_clave = profesor_clave;
    }
}
