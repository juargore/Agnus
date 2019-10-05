package com.proj.agnus.adaptadores;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Cal_Alumno_Evaluacion;

import java.util.List;

/**
 * Created by Arturo on 5/30/2018.
 */

public class Adaptador_Cal_Alumno_Evaluacion extends RecyclerView.Adapter<Adaptador_Cal_Alumno_Evaluacion.Adaptador_Cal_Alumno_EvaluacionViewHolder>{

    private List<Cal_Alumno_Evaluacion> cal_alumno_evaluacion;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Cal_Alumno_Evaluacion(List<Cal_Alumno_Evaluacion> cal_alumno_evaluacion, OnItemClickListener onItemClickListener) {
        this.cal_alumno_evaluacion = cal_alumno_evaluacion;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Cal_Alumno_EvaluacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_cal_evaluacion, parent, false);
        return new Adaptador_Cal_Alumno_EvaluacionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Cal_Alumno_Evaluacion.Adaptador_Cal_Alumno_EvaluacionViewHolder holder, int position) {
        holder.bind(cal_alumno_evaluacion.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return cal_alumno_evaluacion.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Cal_Alumno_Evaluacion cal_alumno_evaluacion);
    }

    public class Adaptador_Cal_Alumno_EvaluacionViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCal_Alumno_Evaluacion;
        private TextView txtCal_Porcentaje_Evaluacion;
        private RelativeLayout rLcal_ColorEval;

        public Adaptador_Cal_Alumno_EvaluacionViewHolder(View itemView) {
            super(itemView);

            txtCal_Alumno_Evaluacion = (TextView) itemView.findViewById(R.id.txtCal_Alumno_Evaluacion);
            txtCal_Porcentaje_Evaluacion = (TextView) itemView.findViewById(R.id.txtCal_Porcentaje_Evaluacion);
            rLcal_ColorEval = (RelativeLayout) itemView.findViewById(R.id.rLcal_ColorEval);
        }

        public void bind(final Cal_Alumno_Evaluacion cal_alumno_evaluacion, final OnItemClickListener listener) {

            txtCal_Alumno_Evaluacion.setText(cal_alumno_evaluacion.obtenerNombre());
            txtCal_Porcentaje_Evaluacion.setText(""+cal_alumno_evaluacion.getCal());
            rLcal_ColorEval.setBackgroundColor(Color.parseColor(cal_alumno_evaluacion.getColor()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(cal_alumno_evaluacion);
                }
            });
        }
    }
}
