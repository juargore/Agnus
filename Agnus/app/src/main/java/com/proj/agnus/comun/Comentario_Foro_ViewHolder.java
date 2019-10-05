package com.proj.agnus.comun;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.proj.agnus.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by Arturo on 6/6/2018.
 */

public class Comentario_Foro_ViewHolder extends ChildViewHolder{

    private TextView txtNombreDebate, txtFechaDebate, txtTextoDebate;

    public Comentario_Foro_ViewHolder(View itemView) {
        super(itemView);

        txtNombreDebate = (TextView) itemView.findViewById(R.id.txtNombreDebate);
        txtFechaDebate = (TextView) itemView.findViewById(R.id.txtFechaDebate);
        txtTextoDebate = (TextView) itemView.findViewById(R.id.txtTextoDebate);
    }

    public void onBind(final Comentario_Foro comentario_foro, Context context){
        txtNombreDebate.setText(comentario_foro.getNombre());
        txtFechaDebate.setText(comentario_foro.getFecha());
        txtTextoDebate.setText(comentario_foro.getTexto());
    }

}
