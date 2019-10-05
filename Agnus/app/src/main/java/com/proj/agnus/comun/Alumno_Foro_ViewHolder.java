package com.proj.agnus.comun;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proj.agnus.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by Arturo on 6/6/2018.
 */

public class Alumno_Foro_ViewHolder extends GroupViewHolder {

    TextView txtNombreHeader, txtCalifHeader;
    LinearLayout lyColorHeader;

    public Alumno_Foro_ViewHolder(View itemView) {
        super(itemView);

        txtNombreHeader = (TextView) itemView.findViewById(R.id.txtNombreHeader);
        txtCalifHeader = (TextView) itemView.findViewById(R.id.txtCalifHeader);
        lyColorHeader = (LinearLayout) itemView.findViewById(R.id.lyColorHeader);
    }

    public void onBind(ExpandableGroup group, Context context){

        String string = group.getTitle();
        String[] parts = string.split("-");
        String nombre = parts[0];
        String calificacion = parts[1];
        String color = parts[2];

        txtNombreHeader.setText("+\t\t"+nombre);
        txtCalifHeader.setText(calificacion);
        lyColorHeader.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void expand() {
        txtNombreHeader.setText("—"+txtNombreHeader.getText().toString().substring(1));
        super.expand();
    }

    @Override
    public void collapse() {
        txtNombreHeader.setText("+"+txtNombreHeader.getText().toString().substring(1));
        super.collapse();
    }
}
