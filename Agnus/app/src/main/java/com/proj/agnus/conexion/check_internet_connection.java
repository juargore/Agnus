package com.proj.agnus.conexion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Arturo on 2/11/2018.
 */

public class check_internet_connection {

    public boolean isConnected(Activity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void MsgInternet(final Activity activity){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage("Esta App necesita conexión a internet para poder funcionar satisfactoriamente. Por favor, conecte su dispositivo a internet e ingrese de nuevo");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Entendido",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        activity.finish();
                        activity.moveTaskToBack(true);

                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
