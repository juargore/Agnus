package com.proj.agnus.adaptadores;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Alumno_Evaluacion;

import java.util.List;

/**
 * Created by Arturo on 5/30/2018.
 */

public class Adaptador_Alumno_Evaluacion extends RecyclerView.Adapter<Adaptador_Alumno_Evaluacion.Adaptador_Alumno_EvaluacionViewHolder>{

    private List<Alumno_Evaluacion> alumno_evaluacion;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Alumno_Evaluacion(List<Alumno_Evaluacion> alumno_evaluacion, OnItemClickListener onItemClickListener) {
        this.alumno_evaluacion = alumno_evaluacion;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Alumno_EvaluacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_asistencias, parent, false);
        return new Adaptador_Alumno_EvaluacionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Alumno_EvaluacionViewHolder holder, int position) {
        holder.bind(alumno_evaluacion.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return alumno_evaluacion.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Alumno_Evaluacion alumno_evaluacion);
    }

    public class Adaptador_Alumno_EvaluacionViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAlumno_Evaluacion;
        private TextView txtCodigo_Evaluacion;
        private TextView txtStatus_Evaluacion;
        private TextView txtPorcentaje_Evaluacion;
        private RelativeLayout rLColorEval;

        public Adaptador_Alumno_EvaluacionViewHolder(View itemView) {
            super(itemView);

            txtAlumno_Evaluacion = (TextView) itemView.findViewById(R.id.txtAlumno_Materia_Asis);
            txtCodigo_Evaluacion = (TextView) itemView.findViewById(R.id.txtCodigo_Profe_Asis);
            txtStatus_Evaluacion = (TextView) itemView.findViewById(R.id.txtStatus_Asis);
            txtPorcentaje_Evaluacion = (TextView) itemView.findViewById(R.id.txtPorcentaje_Asis);
            rLColorEval = (RelativeLayout) itemView.findViewById(R.id.rLColorAsis);
        }

        public void bind(final Alumno_Evaluacion alumno_evaluacion, final OnItemClickListener listener) {

            txtAlumno_Evaluacion.setText(alumno_evaluacion.obtenerNombre());
            txtCodigo_Evaluacion.setText(""+alumno_evaluacion.getArea());
            txtStatus_Evaluacion.setText(alumno_evaluacion.getStatus());
            txtPorcentaje_Evaluacion.setText(""+alumno_evaluacion.getCal());
            rLColorEval.setBackgroundColor(Color.parseColor(alumno_evaluacion.getColor()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(alumno_evaluacion);
                }
            });
        }
    }
}
