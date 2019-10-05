package com.proj.agnus.adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.comun.Email;
import com.proj.agnus.comun.Imagen_Circular;
import com.proj.agnus.fragments.Fragment_Email;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Arturo on 3/22/2018.
 */

public class Adaptador_Email extends RecyclerView.Adapter<Adaptador_Email.AdaptadorEmailViewHolder> {

    private List<Email> emails;
    private static Context context;
    Activity activity;
    Fragment_Email fragment;
    private OnItemClickListener onItemClickListener;

    public Adaptador_Email(List<Email> emails, Context context, Activity activity, Fragment_Email fragment, OnItemClickListener onItemClickListener) {
        this.emails = emails;
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Adaptador_Email.AdaptadorEmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantilla_celdas_email, parent, false);
        return new AdaptadorEmailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Email.AdaptadorEmailViewHolder holder, int position) {
        holder.bind(emails.get(position), onItemClickListener, position);
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Email email);
    }

    public class AdaptadorEmailViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPersonaEmail;
        private TextView txtBolitaRecibidoEmail;
        private TextView txtNombreEmail;
        private TextView txtAsuntoEmail;
        private TextView txtMensajeEmail;
        private TextView txtFechaCortaEmail;

        public AdaptadorEmailViewHolder(View itemView) {
            super(itemView);

            ivPersonaEmail = (ImageView) itemView.findViewById(R.id.ivPersonaEmail);
            txtBolitaRecibidoEmail = (TextView) itemView.findViewById(R.id.txtBolitaRecibidoEmail);
            txtNombreEmail = (TextView) itemView.findViewById(R.id.txtNombreEmail);
            txtAsuntoEmail = (TextView) itemView.findViewById(R.id.txtAsuntoEmail);
            txtMensajeEmail = (TextView) itemView.findViewById(R.id.txtMensajeEmail);
            txtFechaCortaEmail = (TextView) itemView.findViewById(R.id.txtFechaCortaEmail);
        }

        public void bind(final Email email, final OnItemClickListener listener, final int position) {

            if(Integer.parseInt(email.getLeido()) == 1){
                txtBolitaRecibidoEmail.setVisibility(View.GONE);
            }

            txtNombreEmail.setText(email.getDe());
            txtAsuntoEmail.setText(email.obtenerNombre());
            txtMensajeEmail.setText(email.getMensaje());
            txtFechaCortaEmail.setText(email.getFecha());

            final Imagen_Circular ic = new Imagen_Circular();
            String imageUri = email.getImg();
            Target mTarget;

            mTarget = new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                    ivPersonaEmail.setImageBitmap(ic.getCircleBitmap(bitmap, activity));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) { }
            };

            Picasso.get().load(imageUri).into(mTarget);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(email);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(activity)
                            .setMessage(("\u00bfEst\u00e1 seguro de eliminar esta Notificacion?"))
                            .setTitle("Eliminar")
                            .setIcon(android.R.drawable.ic_menu_delete)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    fragment.eliminarEmail(email.obtenerId());
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();

                    return true;
                }
            });
        }
    }
}
