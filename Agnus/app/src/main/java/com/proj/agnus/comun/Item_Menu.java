package com.proj.agnus.comun;

/**
 * Created by Arturo on 2/11/2018.
 */

public class Item_Menu {

    private String titulo;
    private int position;
    private int icono;
    private String contador = "0";
    private boolean contadorVisible = false;

    public Item_Menu(String title, int icon){
        this.titulo = title;
        this.icono = icon;
    }

    public Item_Menu(int position, String title, int icon, boolean isCounterVisible, String count){
        this.position = position;
        this.titulo = title;
        this.icono = icon;
        this.contadorVisible = isCounterVisible;
        this.contador = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getContador() {
        return contador;
    }

    public void setContador(String contador) {
        this.contador = contador;
    }

    public boolean isContadorVisible() {
        return contadorVisible;
    }

    public void setContadorVisible(boolean contadorVisible) {
        this.contadorVisible = contadorVisible;
    }

    public boolean getCounterVisibility(){
        return this.contadorVisible;
    }
}
