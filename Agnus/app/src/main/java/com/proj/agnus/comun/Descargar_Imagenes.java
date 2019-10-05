package com.proj.agnus.comun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Created by Arturo on 2/12/2018.
 */

public class Descargar_Imagenes{

    public Bitmap obtenerImagen(String url) throws IOException {

        Bitmap imagen;
        InputStream imagenEntrante;

        if(url.contains("http://")){
            url = url.replace("http://","https://");
            url = url.replace(" ","%20");
        } else if (url.contains("https://")){
            url = url.replace(" ","%20");
        } else {
            url = "https://"+url.replace(" ","%20");
        }

        //Log.e("URL",""+url);
        imagenEntrante = new URL(url).openStream();
        imagen = BitmapFactory.decodeStream(imagenEntrante);

        if(imagenEntrante != null) imagenEntrante.close();

        return imagen;
    }
}
