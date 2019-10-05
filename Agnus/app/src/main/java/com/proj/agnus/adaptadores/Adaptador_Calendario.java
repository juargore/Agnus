package com.proj.agnus.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Calendario;

import java.util.List;

/**
 * Created by Arturo on 3/15/2018.
 */

public class Adaptador_Calendario extends RecyclerView.Adapter<Adaptador_Calendario.Adaptador_CalendarioViewHolder> {

    private List<Calendario> evento_calendario;
    private static Context context;
    private OnItemClickListener onItemClickListener;
    private int tipo_usuario;

    public Adaptador_Calendario(List<Calendario> evento_calendario, Context context, OnItemClickListener onItemClickListener) {

        this.context = context;
        this.evento_calendario = evento_calendario;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Calendario.Adaptador_CalendarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_calendario, parent, false);
        return new Adaptador_CalendarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Calendario.Adaptador_CalendarioViewHolder holder, int position) {
        holder.bind(evento_calendario.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return evento_calendario.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Calendario evento_calendario);
    }

    public class Adaptador_CalendarioViewHolder extends RecyclerView.ViewHolder {

        private TextView txtActividadCalendario;
        private TextView txtDescriptionCalendario;
        private TextView txtFechaInCalendario;
        private TextView txtHoraInCalendario;
        private TextView txtFechaFinCalendario;
        private TextView txtHoraFinCalendario;


        public Adaptador_CalendarioViewHolder(View itemView) {
            super(itemView);

            txtActividadCalendario = (TextView) itemView.findViewById(R.id.txtActividadCalendario);
            txtDescriptionCalendario = (TextView) itemView.findViewById(R.id.txtDescriptionCalendario);
            txtFechaInCalendario = (TextView) itemView.findViewById(R.id.txtFechaInCalendario);
            txtHoraInCalendario = (TextView) itemView.findViewById(R.id.txtHoraInCalendario);
            txtFechaFinCalendario = (TextView) itemView.findViewById(R.id.txtFechaFinCalendario);
            txtHoraFinCalendario = (TextView) itemView.findViewById(R.id.txtHoraFinCalendario);
        }

        public void bind(final Calendario evento_calendario, final OnItemClickListener listener) {
            txtActividadCalendario.setText(evento_calendario.obtenerNombre());
            txtDescriptionCalendario.setText(""+evento_calendario.getDescripcion());
            txtFechaInCalendario.setText(""+evento_calendario.getFecha_ini());
            txtHoraInCalendario.setText(""+evento_calendario.getHora_ini());
            txtFechaFinCalendario.setText(""+evento_calendario.getFecha_fin());
            txtHoraFinCalendario.setText(""+evento_calendario.getHora_fin());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(evento_calendario);
                }
            });
        }
    }
}
