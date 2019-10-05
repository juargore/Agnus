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
import com.proj.agnus.comun.Asistencia_Alumno_Detalle;

import java.util.List;

/**
 * Created by Arturo on 2/26/2018.
 */

public class Adaptador_Asistencias_Detalle extends RecyclerView.Adapter<Adaptador_Asistencias_Detalle.Adaptador_Asistencias_DetalleViewHolder> {

    private List<Asistencia_Alumno_Detalle> asistencia_alumno_detalle;
    private static Context context;
    private int tipo_usuario;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Asistencias_Detalle(int tipo_usuario, List<Asistencia_Alumno_Detalle> asistencia_alumno_detalle, Context context,
                                         OnItemClickListener onItemClickListener) {

        this.context = context;
        this.asistencia_alumno_detalle = asistencia_alumno_detalle;
        this.onItemClickListener = onItemClickListener;
        this.tipo_usuario = tipo_usuario;
    }

    @Override
    public Adaptador_Asistencias_Detalle.Adaptador_Asistencias_DetalleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_asistencias_detalle, parent, false);
        return new Adaptador_Asistencias_DetalleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Asistencias_Detalle.Adaptador_Asistencias_DetalleViewHolder holder, int position) {
        holder.bind(asistencia_alumno_detalle.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return asistencia_alumno_detalle.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Asistencia_Alumno_Detalle asistencia_alumno_detalle);
    }

    public class Adaptador_Asistencias_DetalleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMes_Asis_Detalle;
        private TextView txtPorcentaje_Asis_Detalle;
        private RelativeLayout rLColorAsis;

        public Adaptador_Asistencias_DetalleViewHolder(View itemView) {
            super(itemView);

            txtMes_Asis_Detalle = (TextView) itemView.findViewById(R.id.txtMes_Asis_Detalle);
            txtPorcentaje_Asis_Detalle = (TextView) itemView.findViewById(R.id.txtPorcentaje_Asis_Detalle);
            rLColorAsis = (RelativeLayout) itemView.findViewById(R.id.rLColorAsis);

        }

        public void bind(final Asistencia_Alumno_Detalle asistencia_alumno_detalle, final OnItemClickListener listener) {

            txtMes_Asis_Detalle.setText(asistencia_alumno_detalle.getFecha());
            txtPorcentaje_Asis_Detalle.setText(asistencia_alumno_detalle.obtenerNombre());
            rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno_detalle.getColor()));


            if(tipo_usuario == 2){
                if(asistencia_alumno_detalle.getTipo() == 1){
                    rLColorAsis.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int estado = asistencia_alumno_detalle.getEstado_actual();

                            if(estado == 0){
                                estado = -1;
                            } else if(estado == 1){
                                estado = 0;
                            } else if(estado == -1){
                                estado = 1;
                            }

                            switch (estado){
                                case -1:
                                    rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno_detalle.getColorm1()));
                                    txtPorcentaje_Asis_Detalle.setText(asistencia_alumno_detalle.getTextom1());
                                    asistencia_alumno_detalle.setEstado_actual(-1);
                                    asistencia_alumno_detalle.setEstado_nuevo(-1);
                                    break;
                                case 0:
                                    rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno_detalle.getColor0()));
                                    txtPorcentaje_Asis_Detalle.setText(asistencia_alumno_detalle.getTexto0());
                                    asistencia_alumno_detalle.setEstado_actual(0);
                                    asistencia_alumno_detalle.setEstado_nuevo(0);
                                    break;
                                case 1:
                                    rLColorAsis.setBackgroundColor(Color.parseColor(asistencia_alumno_detalle.getColor1()));
                                    txtPorcentaje_Asis_Detalle.setText(asistencia_alumno_detalle.getTexto1());
                                    asistencia_alumno_detalle.setEstado_actual(1);
                                    asistencia_alumno_detalle.setEstado_nuevo(1);
                                    break;
                            }

                            listener.OnItemClick(asistencia_alumno_detalle);
                        }
                    });
                }
            }
        }
    }
}

