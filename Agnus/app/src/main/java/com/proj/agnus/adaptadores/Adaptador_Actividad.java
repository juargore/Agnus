package com.proj.agnus.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Actividad;

import java.util.List;

/**
 * Created by Arturo on 6/7/2018.
 */

public class Adaptador_Actividad extends RecyclerView.Adapter<Adaptador_Actividad.Adaptador_ActividadViewHolder>{

    private List<Actividad> actividad;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Actividad(List<Actividad> actividad, Context context, OnItemClickListener onItemClickListener){
        this.context = context;
        this.actividad = actividad;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public Adaptador_ActividadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_asistencias, parent, false);
        return new Adaptador_ActividadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_ActividadViewHolder holder, int position) {
        holder.bind(actividad.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return actividad.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Actividad actividad);
    }

    public class Adaptador_ActividadViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNombreAct;
        private TextView txtArchivoAct;
        private TextView txtCalAct;
        private RelativeLayout rLColorAct;
        private TextView txtStatusAct;

        public Adaptador_ActividadViewHolder(View itemView) {
            super(itemView);

            txtNombreAct = (TextView) itemView.findViewById(R.id.txtAlumno_Materia_Asis);
            txtArchivoAct = (TextView) itemView.findViewById(R.id.txtCodigo_Profe_Asis);
            txtCalAct = (TextView) itemView.findViewById(R.id.txtPorcentaje_Asis);
            rLColorAct = (RelativeLayout) itemView.findViewById(R.id.rLColorAsis);
            txtStatusAct = (TextView) itemView.findViewById(R.id.txtStatus_Asis);
        }

        public void bind(final Actividad actividad, final OnItemClickListener listener) {
            txtNombreAct.setText(actividad.obtenerNombre());
            txtArchivoAct.setText(""+actividad.getArchivo());
            txtCalAct.setText(actividad.getCal());
            rLColorAct.setBackgroundColor(Color.parseColor(actividad.getColor()));
            if(actividad.getExtension() == null){
                txtStatusAct.setText(""+actividad.getStatus());
            } else{
                txtStatusAct.setText(""+actividad.getExtension());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(actividad);
                }
            });
        }
    }
}
