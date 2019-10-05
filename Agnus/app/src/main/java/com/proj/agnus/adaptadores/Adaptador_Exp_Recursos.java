package com.proj.agnus.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Recursos;

import java.util.List;

/**
 * Created by Arturo on 6/5/2018.
 */

public class Adaptador_Exp_Recursos extends RecyclerView.Adapter<Adaptador_Exp_Recursos.Adaptador_Exp_RecursosViewHolder>{

    private List<Recursos> lista;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Exp_Recursos(List<Recursos> lista, OnItemClickListener onItemClickListener){
        this.lista = lista;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Exp_RecursosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_subitem_recursos, parent, false);
        return new Adaptador_Exp_RecursosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Exp_RecursosViewHolder holder, int position) {
        holder.bind(lista.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Recursos recursos);
    }

    public class Adaptador_Exp_RecursosViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTituloExp;
        private TextView txtDescExp;
        private TextView txtTipoExp;

        public Adaptador_Exp_RecursosViewHolder(View itemView) {
            super(itemView);

            txtTituloExp = (TextView) itemView.findViewById(R.id.txtTituloExp);
            txtDescExp = (TextView) itemView.findViewById(R.id.txtDescExp);
            txtTipoExp = (TextView) itemView.findViewById(R.id.txtTipoExp);
        }

        public void bind(final Recursos recursos, final OnItemClickListener listener) {
            txtTituloExp.setText(recursos.obtenerNombre());
            txtDescExp.setText(""+recursos.getDescripcion());
            txtTipoExp.setText(""+recursos.getLabel());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(recursos);
                }
            });
        }
    }
}
