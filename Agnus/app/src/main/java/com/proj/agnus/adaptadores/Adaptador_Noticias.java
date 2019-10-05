package com.proj.agnus.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Noticias;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Arturo on 2/21/2018.
 */

public class Adaptador_Noticias extends RecyclerView.Adapter<Adaptador_Noticias.AdaptadorNoticiasViewHolder> {

    private List<Noticias> noticias;
    private static Context context;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Noticias(List<Noticias> noticias, Context context, OnItemClickListener onItemClickListener) {
        this.noticias = noticias;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Noticias.AdaptadorNoticiasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_noticias, parent, false);
        return new AdaptadorNoticiasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Noticias.AdaptadorNoticiasViewHolder holder, int position) {
        holder.bind(noticias.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Noticias noticia);
    }

    public class AdaptadorNoticiasViewHolder extends RecyclerView.ViewHolder {

        private TextView txtFechaNoticias;
        private TextView txtSubtituloNoticias;
        private TextView txtTituloNoticias;
        private ImageView imgFotoNoticias;

        public AdaptadorNoticiasViewHolder(View itemView) {
            super(itemView);

            txtTituloNoticias = (TextView) itemView.findViewById(R.id.txtTituloNoticias);
            txtSubtituloNoticias = (TextView) itemView.findViewById(R.id.txtSubtituloNoticias);
            txtFechaNoticias = (TextView) itemView.findViewById(R.id.txtFechaNoticias);
            imgFotoNoticias = (ImageView) itemView.findViewById(R.id.imgFotoNoticias);
        }

        public void bind(final Noticias Noticias, final OnItemClickListener listener) {
            txtTituloNoticias.setText(Noticias.obtenerNombre());
            txtSubtituloNoticias.setText(Noticias.getDescripcion());
            txtFechaNoticias.setText(Noticias.getFecha());

            String imageUri = Noticias.getImagen();
            Picasso.get().load(imageUri).into(imgFotoNoticias);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(Noticias);
                }
            });
        }
    }
}
