package com.proj.agnus.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proj.agnus.R;
import com.proj.agnus.comun.Alumno_Foro_ViewHolder;
import com.proj.agnus.comun.Comentario_Foro;
import com.proj.agnus.comun.Comentario_Foro_ViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Arturo on 6/6/2018.
 */

public class Adaptador_Foro extends ExpandableRecyclerViewAdapter<Alumno_Foro_ViewHolder, Comentario_Foro_ViewHolder> {

    private Context context;

    public Adaptador_Foro(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);

        this.context = context;
    }

    //Vista de Encabezados
    @Override
    public Alumno_Foro_ViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_header,parent,false);
        return new Alumno_Foro_ViewHolder(itemView);
    }

    //Vista de Sub Items
    @Override
    public Comentario_Foro_ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_debate,parent,false);
        return new Comentario_Foro_ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    //Acciones de los Encabezados
    @Override
    public void onBindGroupViewHolder(Alumno_Foro_ViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.onBind(group, context);
    }

    //Acciones de los Sub Items
    @Override
    public void onBindChildViewHolder(Comentario_Foro_ViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        Comentario_Foro comentario = (Comentario_Foro) group.getItems().get(childIndex);
        holder.onBind(comentario, context);
    }
}
