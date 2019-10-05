package com.proj.agnus.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Item_Menu;

import java.util.ArrayList;

/**
 * Created by Arturo on 2/11/2018.
 */

public class Adaptador_NavDrawer_List extends BaseAdapter {

    private Context context;
    private ArrayList<Item_Menu> lista_items_menu;
    private int tipo_usuario;

    public Adaptador_NavDrawer_List(Context context, ArrayList<Item_Menu> lista_items_menu, int tipo_usuario){
        this.context = context;
        this.lista_items_menu = lista_items_menu;
        this.tipo_usuario = tipo_usuario;
    }

    @Override
    public int getCount() {
        return lista_items_menu.size();
    }

    @Override
    public Object getItem(int position) {
        return lista_items_menu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.elemento_listado_menu_lateral, null);

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icono);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.titulo);
        TextView txtCount = (TextView) convertView.findViewById(R.id.contador);

        int imageResource = lista_items_menu.get(position).getIcono();
        String titulo = lista_items_menu.get(position).getTitulo();
        int contador = Integer.parseInt(lista_items_menu.get(position).getContador());

        imgIcon.setImageResource(imageResource);
        txtTitle.setText(titulo);

        if(contador > 0){
            txtCount.setText(""+contador);
        } else {
            txtCount.setVisibility(View.GONE);
        }

        if(titulo.equals("Calendario")){
            if(tipo_usuario > 2){
                convertView = mInflater.inflate(R.layout.elemento_nulo, null);
            }
        }

        if(titulo.equals("Horarios") || titulo.equals("Evaluación") || titulo.equals("Asistencias")){
            if(tipo_usuario > 3 && tipo_usuario < 6){
                convertView = mInflater.inflate(R.layout.elemento_nulo, null);
            }
        }

        return convertView;
    }
}