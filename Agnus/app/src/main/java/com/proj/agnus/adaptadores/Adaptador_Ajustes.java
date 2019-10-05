package com.proj.agnus.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Ajustes;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arturo on 6/20/2018.
 */

public class Adaptador_Ajustes extends  RecyclerView.Adapter<Adaptador_Ajustes.Adaptador_Ajustes_ViewHolder>{

    private List<Ajustes> lista_ajustes;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Ajustes(List<Ajustes> lista_ajustes, OnItemClickListener onItemClickListener) {
        this.lista_ajustes = lista_ajustes;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Ajustes_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_ajustes, parent, false);
        return new Adaptador_Ajustes_ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Ajustes_ViewHolder holder, int position) {
        holder.bind(lista_ajustes.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return lista_ajustes.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Ajustes ajustes);
    }


    public class Adaptador_Ajustes_ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtNombreAjustes;
        private TextView txtSubtituloAjustes;
        private ImageView ivIconosAjustes;

        public Adaptador_Ajustes_ViewHolder(View itemView) {
            super(itemView);

            txtNombreAjustes = (TextView) itemView.findViewById(R.id.txtNombreAjustes);
            txtSubtituloAjustes = (TextView) itemView.findViewById(R.id.txtSubtituloAjustes);
            ivIconosAjustes = (ImageView) itemView.findViewById(R.id.ivIconosAjustes);
        }

        public void bind(final Ajustes ajustes, final OnItemClickListener listener){
            txtNombreAjustes.setText(ajustes.obtenerNombre());
            txtSubtituloAjustes.setText(ajustes.getSubtitulo());

            String icono = ajustes.getIcono();
            Picasso.get().load(icono).into(ivIconosAjustes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(ajustes);
                }
            });
        }
    }
}
