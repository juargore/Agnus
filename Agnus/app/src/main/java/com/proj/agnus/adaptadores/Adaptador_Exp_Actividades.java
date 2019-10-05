package com.proj.agnus.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Actividades;

import java.util.List;

/**
 * Created by Arturo on 6/5/2018.
 */

public class Adaptador_Exp_Actividades extends RecyclerView.Adapter<Adaptador_Exp_Actividades.Adaptador_Exp_ActividadesViewHolder>{

    private List<Actividades> lista;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Exp_Actividades(List<Actividades> lista, OnItemClickListener onItemClickListener){
        this.lista = lista;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Exp_ActividadesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_subitem_actividades, parent, false);
        return new Adaptador_Exp_ActividadesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Exp_ActividadesViewHolder holder, int position) {
        holder.bind(lista.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Actividades actividades);
    }

    public class Adaptador_Exp_ActividadesViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTituloAct;
        private TextView txtTipoAct;

        public Adaptador_Exp_ActividadesViewHolder(View itemView) {
            super(itemView);

            txtTituloAct = (TextView) itemView.findViewById(R.id.txtTituloAct);
            txtTipoAct = (TextView) itemView.findViewById(R.id.txtTipoAct);
        }

        public void bind(final Actividades actividades, final OnItemClickListener listener) {
            txtTituloAct.setText(actividades.obtenerNombre());
            txtTipoAct.setText(""+actividades.getLabel());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(actividades);
                }
            });
        }
    }
}
