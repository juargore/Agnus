package com.proj.agnus.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.activity.Agregar_Asistencia;
import com.proj.agnus.comun.Asistencia_Alumno;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Arturo on 2/26/2018.
 */

public class Adaptador_Asistencias extends RecyclerView.Adapter<Adaptador_Asistencias.Adaptador_AsistenciasViewHolder> {

    private List<Asistencia_Alumno> asistencia_alumno;
    private static Context context;
    private OnItemClickListener onItemClickListener;
    private int tipo_usuario, procedencia;

    public Adaptador_Asistencias(List<Asistencia_Alumno> asistencia_alumno, Context context,
                                 OnItemClickListener onItemClickListener, int tipo_usuario, int procedencia) {

        this.context = context;
        this.tipo_usuario = tipo_usuario;
        this.asistencia_alumno = asistencia_alumno;
        this.onItemClickListener = onItemClickListener;
        this.procedencia = procedencia;
    }

    @Override
    public Adaptador_Asistencias.Adaptador_AsistenciasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_asistencias, parent, false);
        return new Adaptador_AsistenciasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Asistencias.Adaptador_AsistenciasViewHolder holder, int position) {
        holder.bind(asistencia_alumno.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return asistencia_alumno.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Asistencia_Alumno asistencia_alumno);
    }

    public class Adaptador_AsistenciasViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAlumno_Materia_Asis;
        private TextView txtCodigo_Profe_Asis;
        private TextView txtStatus_Asis;
        private TextView txtPorcentaje_Asis;

        private RelativeLayout rLColorAsis;


        public Adaptador_AsistenciasViewHolder(View itemView) {
            super(itemView);

            txtAlumno_Materia_Asis = (TextView) itemView.findViewById(R.id.txtAlumno_Materia_Asis);
            txtCodigo_Profe_Asis = (TextView) itemView.findViewById(R.id.txtCodigo_Profe_Asis);
            txtStatus_Asis = (TextView) itemView.findViewById(R.id.txtStatus_Asis);
            txtPorcentaje_Asis = (TextView) itemView.findViewById(R.id.txtPorcentaje_Asis);
            rLColorAsis = (RelativeLayout) itemView.findViewById(R.id.rLColorAsis);
        }

        public void bind(final Asistencia_Alumno asistencia_alumno, final OnItemClickListener listener) {

            txtAlumno_Materia_Asis.setText(asistencia_alumno.obtenerNombre());
            txtCodigo_Profe_Asis.setText(""+asistencia_alumno.getCodigo());
            if(tipo_usuario == 2){
                txtStatus_Asis.setText(asistencia_alumno.getStatus());
            }else{
                txtStatus_Asis.setVisibility(View.INVISIBLE);
            }


            if(procedencia == 1){
                txtPorcentaje_Asis.setText(""+asistencia_alumno.getTot_asis_alum()+"  "+asistencia_alumno.getPorcentaje());
                rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno.getColor()));
            } else if (procedencia == 2){
                txtPorcentaje_Asis.setText("Asistencia");
                rLColorAsis.setBackgroundColor(Color.parseColor("#00A759"));
            }


            if(procedencia == 1){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnItemClick(asistencia_alumno);
                    }
                });
            } else if(procedencia == 2){
                rLColorAsis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int estado = asistencia_alumno.getEstado_actual();

                        if(estado == 0){
                            estado = -1;
                        } else if(estado == 1){
                            estado = 0;
                        } else if(estado == -1){
                            estado = 1;
                        }

                        switch (estado){
                            case -1:
                                rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno.getColorm1()));
                                txtPorcentaje_Asis.setText(asistencia_alumno.getTextom1());
                                asistencia_alumno.setEstado_actual(-1);
                                asistencia_alumno.setEstado_nuevo(-1);
                                break;
                            case 0:
                                rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno.getColor0()));
                                txtPorcentaje_Asis.setText(asistencia_alumno.getTexto0());
                                asistencia_alumno.setEstado_actual(0);
                                asistencia_alumno.setEstado_nuevo(0);
                                break;
                            case 1:
                                rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno.getColor1()));
                                txtPorcentaje_Asis.setText(asistencia_alumno.getTexto1());
                                asistencia_alumno.setEstado_actual(1);
                                asistencia_alumno.setEstado_nuevo(1);
                                break;
                        }

                        listener.OnItemClick(asistencia_alumno);
                    }
                });
            }

        }
    }
}

