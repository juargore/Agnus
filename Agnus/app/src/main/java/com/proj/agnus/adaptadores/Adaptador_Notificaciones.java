package com.proj.agnus.adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Descargar_Imagenes;
import com.proj.agnus.comun.Notificacion;
import com.proj.agnus.fragments.Fragment_Notificaciones;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Arturo on 2/21/2018.
 */

public class Adaptador_Notificaciones extends RecyclerView.Adapter<Adaptador_Notificaciones.AdaptadorNotificacionesViewHolder> {

    private List<Notificacion> notificacion;
    private static Context context;
    private OnItemClickListener onItemClickListener;
    private Activity activity;
    private Fragment_Notificaciones fragment;
    Descargar_Imagenes di = new Descargar_Imagenes();

    public Adaptador_Notificaciones(List<Notificacion> notificacion, Context context, Activity activity, Fragment_Notificaciones fragment, OnItemClickListener onItemClickListener) {
        this.notificacion = notificacion;
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Notificaciones.AdaptadorNotificacionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_notificaciones, parent, false);
        return new AdaptadorNotificacionesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Notificaciones.AdaptadorNotificacionesViewHolder holder, int position) {
        holder.bind(notificacion.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return notificacion.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Notificacion notificacion);
    }

    public class AdaptadorNotificacionesViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCuerpoNoti;
        private TextView txtFechaNoti;
        private TextView txtHoraNoti;
        private ImageView ivIconosNoti;

        public AdaptadorNotificacionesViewHolder(View itemView) {
            super(itemView);

            txtCuerpoNoti = (TextView) itemView.findViewById(R.id.txtCuerpoNoti);
            txtFechaNoti = (TextView) itemView.findViewById(R.id.txtFechaNoti);
            txtHoraNoti = (TextView) itemView.findViewById(R.id.txtHoraNoti);
            ivIconosNoti = (ImageView) itemView.findViewById(R.id.ivIconosNoti);
        }

        public void bind(final Notificacion notificacion, final OnItemClickListener listener) {
            txtCuerpoNoti.setText(notificacion.obtenerNombre());
            txtFechaNoti.setText(notificacion.getFecha());
            txtHoraNoti.setText(notificacion.getHora());

            if(Integer.parseInt(notificacion.getLeido()) == 0){
                txtCuerpoNoti.setTypeface(null, Typeface.BOLD);
            }

            String imageUri = notificacion.getIcono();
            Picasso.get().load(imageUri).into(ivIconosNoti);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(notificacion);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(activity)
                            .setMessage(("\u00bfEst\u00e1 seguro de eliminar este Email?"))
                            .setTitle("Eliminar")
                            .setIcon(android.R.drawable.ic_menu_delete)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    fragment.eliminarNotificacion(notificacion.obtenerId());
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();

                    return true;
                }
            });
        }
    }

    /*private class TareaAsincrona extends AsyncTask<Void, Integer, Bitmap> {

        ImageView imgFotoNotificaciones;
        String imageUri;
        
        public TareaAsincrona(ImageView imgFotoNotificaciones, String imageUri){
            this.imgFotoNotificaciones = imgFotoNotificaciones;
            this.imageUri = imageUri;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            Descargar_Imagenes di = new Descargar_Imagenes();
            Bitmap b = null;
            
            try {
                b = di.obtenerImagen(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            imgFotoNotificaciones.setImageBitmap(b);
        }
    }*/
}
