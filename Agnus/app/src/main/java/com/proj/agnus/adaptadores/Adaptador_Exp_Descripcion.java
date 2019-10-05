package com.proj.agnus.adaptadores;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.comun.Periodo;

import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Arturo on 6/5/2018.
 */

public class Adaptador_Exp_Descripcion extends RecyclerView.Adapter<Adaptador_Exp_Descripcion.Adaptador_Exp_DescripcionViewHolder> {

    private List<Periodo> descripcion;
    private Context context;

    public Adaptador_Exp_Descripcion(List<Periodo> descripcion, Context context){
        this.descripcion = descripcion;
        this.context = context;
    }

    @Override
    public Adaptador_Exp_DescripcionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_subitem_descripcion, parent, false);
        return new Adaptador_Exp_DescripcionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adaptador_Exp_DescripcionViewHolder holder, int position) {
        holder.bind(descripcion.get(position));
    }

    @Override
    public int getItemCount() {
        return descripcion.size();
    }


    public class Adaptador_Exp_DescripcionViewHolder extends RecyclerView.ViewHolder {

        private WebView webViewDescripcionExp;

        public Adaptador_Exp_DescripcionViewHolder(View itemView) {
            super(itemView);

            webViewDescripcionExp = (WebView) itemView.findViewById(R.id.webViewDescripcionExp);
        }

        public void bind(final Periodo data) {
            //Configuración del webView
            webViewDescripcionExp.setWebViewClient(new WebViewClient());
            webViewDescripcionExp.setWebChromeClient(new WebChromeClient(){
                public void onProgressChanged(WebView view, int progress) {
                    webViewDescripcionExp.getSettings().setJavaScriptEnabled(true);
                    webViewDescripcionExp.getSettings().setLoadsImagesAutomatically(true);
                    webViewDescripcionExp.getSettings().setDomStorageEnabled(true);
                    webViewDescripcionExp.getSettings().setAllowFileAccess(true);
                    webViewDescripcionExp.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                }
            });

            // Setting on Touch Listener for handling the touch inside ScrollView
            webViewDescripcionExp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            //Listener para poder descargar archivos adjuntos del webView
            webViewDescripcionExp.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                            String mimetype, long contentLength) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Agnus: Descarga de archivos");

                    DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(context, "Descargando archivo...", Toast.LENGTH_LONG).show();
                }
            });

            webViewDescripcionExp.loadData(data.obtenerNombre(), "text/html; charset=utf-8", "utf-8");
        }
    }

}
