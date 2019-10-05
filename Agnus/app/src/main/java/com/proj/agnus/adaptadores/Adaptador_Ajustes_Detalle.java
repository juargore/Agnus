package com.proj.agnus.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Ajustes_Detalles;

import java.util.List;

/**
 * Created by Arturo on 6/20/2018.
 */

public class Adaptador_Ajustes_Detalle extends  RecyclerView.Adapter<Adaptador_Ajustes_Detalle.Adaptador_Ajustes_ViewHolder>{

    private List<Ajustes_Detalles> lista_ajustes;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Ajustes_Detalle(List<Ajustes_Detalles> lista_ajustes, OnItemClickListener onItemClickListener) {
        this.lista_ajustes = lista_ajustes;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Ajustes_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_ajustes_detalle, parent, false);
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
        void OnItemClick(Ajustes_Detalles ajustes);
    }


    public class Adaptador_Ajustes_ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtNombreAjusteDetalle;
        private Switch switchAjustes;

        public Adaptador_Ajustes_ViewHolder(View itemView) {
            super(itemView);

            txtNombreAjusteDetalle = (TextView) itemView.findViewById(R.id.txtNombreAjusteDetalle);
            switchAjustes = (Switch) itemView.findViewById(R.id.switchAjustes);
        }

        public void bind(final Ajustes_Detalles ajustes, final OnItemClickListener listener){
            txtNombreAjusteDetalle.setText(ajustes.obtenerNombre());

            if(ajustes.getValor() == 0){
                switchAjustes.setChecked(false);
            } else {
                switchAjustes.setChecked(true);
            }

            /*switchAjustes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // do something, the isChecked will be true if the switch is in the On position

                }
            });*/

            switchAjustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(ajustes);
                }
            });
        }
    }
}
