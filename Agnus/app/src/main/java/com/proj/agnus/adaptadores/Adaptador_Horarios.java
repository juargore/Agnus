package com.proj.agnus.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Horario;

import java.util.List;

/**
 * Created by Arturo on 2/26/2018.
 */

public class Adaptador_Horarios extends RecyclerView.Adapter<Adaptador_Horarios.Adaptador_HorariosViewHolder> {

    private List<Horario> horario_alumno;
    private static Context context;
    private OnItemClickListener onItemClickListener;
    private int tipo_usuario;

    public Adaptador_Horarios(List<Horario> horario_alumno, Context context, OnItemClickListener onItemClickListener, int tipo_usuario) {

        this.context = context;
        this.tipo_usuario = tipo_usuario;
        this.horario_alumno = horario_alumno;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Horarios.Adaptador_HorariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_horario_alumnos, parent, false);
        return new Adaptador_HorariosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Horarios.Adaptador_HorariosViewHolder holder, int position) {
        holder.bind(horario_alumno.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return horario_alumno.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Horario horario_alumno);
    }

    public class Adaptador_HorariosViewHolder extends RecyclerView.ViewHolder {

        private TextView txtHoraHorario;
        private TextView txtAulaHorario;
        private TextView txtMateriaHorario;
        private TextView txtProfeClaveHorario;
        private TextView txtGrupoHorario;


        public Adaptador_HorariosViewHolder(View itemView) {
            super(itemView);

            txtHoraHorario = (TextView) itemView.findViewById(R.id.txtHoraHorario);
            txtAulaHorario = (TextView) itemView.findViewById(R.id.txtAulaHorario);
            txtMateriaHorario = (TextView) itemView.findViewById(R.id.txtMateriaHorario);
            txtProfeClaveHorario = (TextView) itemView.findViewById(R.id.txtProfeClaveHorario);
            txtGrupoHorario = (TextView) itemView.findViewById(R.id.txtGrupoHorario);
        }

        public void bind(final Horario horario_alumno, final OnItemClickListener listener) {
            txtHoraHorario.setText(horario_alumno.getHora());
            txtAulaHorario.setText(""+horario_alumno.getSalon());
            txtMateriaHorario.setText(horario_alumno.obtenerNombre());
            txtProfeClaveHorario.setText(""+horario_alumno.getProfesorClave());
            txtGrupoHorario.setText(""+horario_alumno.getGrupo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(horario_alumno);
                }
            });
        }
    }
}

