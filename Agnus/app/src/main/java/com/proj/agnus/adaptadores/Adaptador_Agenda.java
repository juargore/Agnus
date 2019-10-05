package com.proj.agnus.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Agenda;

import java.util.List;

/**
 * Created by Arturo on 3/15/2018.
 */

public class Adaptador_Agenda extends RecyclerView.Adapter<Adaptador_Agenda.Adaptador_AgendaViewHolder> {

    private List<Agenda> evento_agenda;
    private static Context context;
    private OnItemClickListener onItemClickListener;
    private int tipo_usuario;

    public Adaptador_Agenda(List<Agenda> evento_agenda, Context context, OnItemClickListener onItemClickListener) {

        this.context = context;
        this.evento_agenda = evento_agenda;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Agenda.Adaptador_AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_agenda, parent, false);
        return new Adaptador_AgendaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Agenda.Adaptador_AgendaViewHolder holder, int position) {
        holder.bind(evento_agenda.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return evento_agenda.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Agenda evento_agenda);
    }

    public class Adaptador_AgendaViewHolder extends RecyclerView.ViewHolder {

        private TextView txtActividadAgenda;
        private TextView txtDescriptionAgenda;
        private TextView txtLugarAgenda;
        private TextView txtFechaInAgenda;
        private TextView txtHoraInAgenda;
        private TextView txtFechaFinAgenda;
        private TextView txtHoraFinAgenda;


        public Adaptador_AgendaViewHolder(View itemView) {
            super(itemView);

            txtActividadAgenda = (TextView) itemView.findViewById(R.id.txtActividadAgenda);
            txtDescriptionAgenda = (TextView) itemView.findViewById(R.id.txtDescriptionAgenda);
            txtLugarAgenda = (TextView) itemView.findViewById(R.id.txtLugarAgenda);
            txtFechaInAgenda = (TextView) itemView.findViewById(R.id.txtFechaInAgenda);
            txtHoraInAgenda = (TextView) itemView.findViewById(R.id.txtHoraInAgenda);
            txtFechaFinAgenda = (TextView) itemView.findViewById(R.id.txtFechaFinAgenda);
            txtHoraFinAgenda = (TextView) itemView.findViewById(R.id.txtHoraFinAgenda);
        }

        public void bind(final Agenda evento_agenda, final OnItemClickListener listener) {
            txtActividadAgenda.setText(evento_agenda.obtenerNombre());
            txtDescriptionAgenda.setText(""+evento_agenda.getDescripcion());
            txtLugarAgenda.setText(evento_agenda.getLugar());
            txtFechaInAgenda.setText(""+evento_agenda.getFecha_ini());
            txtHoraInAgenda.setText(""+evento_agenda.getHora_ini());
            txtFechaFinAgenda.setText(""+evento_agenda.getFecha_fin().replace(" ",""));
            txtHoraFinAgenda.setText(""+evento_agenda.getHora_fin());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(evento_agenda);
                }
            });
        }
    }
}
