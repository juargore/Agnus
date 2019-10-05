package com.proj.agnus.servicios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.proj.agnus.R;
import com.proj.agnus.activity.MainActivity;
import com.proj.agnus.activity.Splash_Screen;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Arturo on 5/3/18.
 */

public class ServicioMensajeriaFirebase extends FirebaseMessagingService {

    private String mensaje = "";
    private String channelId = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //From console (Jesus)
        if (remoteMessage.getNotification() != null) {
            Log.e("--","Notificacion recibida en SMFirebase: "+remoteMessage.getNotification().getBody());
            mensaje = remoteMessage.getNotification().getBody();
            channelId = "100";
        }

        //From Server (Firebase)
        /*if(!remoteMessage.getData().isEmpty()){
            Log.e("Data",""+remoteMessage.getData().toString());
            mensaje = remoteMessage.getData().toString();
        }*/

        Intent intent = new Intent(this, Splash_Screen.class);
        intent.putExtra("activity_name","Notificacion_Local");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.agnus);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            showNotification(getApplication(), pendingIntent, mensaje, intent, customSoundUri);
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icono_agnus_fondo_blanco_y_redondeado)
                .setContentText(""+mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(customSoundUri)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, mBuilder.build());
        }

    }

    public void showNotification(Context context, PendingIntent pendingIntent, String body, Intent intent, Uri sound) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icono_agnus_fondo_blanco_y_redondeado)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
