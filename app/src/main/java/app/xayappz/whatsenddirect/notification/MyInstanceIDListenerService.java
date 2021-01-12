package app.xayappz.whatsenddirect.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import app.xayappz.whatsenddirect.R;
import app.xayappz.whatsenddirect.ui.MainActivity;

public class MyInstanceIDListenerService extends FirebaseMessagingService {

    String refreshedToken;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "app.xayappz.whatsenddirect";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Notification2 = value",

                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("WhatSend Direct");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {


            notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentInfo("New Notification")
                    .setSmallIcon(R.drawable.ic_stat_notifications_active)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setColor(Color.parseColor("#FFD81B60"))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        } else {




            notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_stat_notifications_active)
                    .setContentInfo("New Notification")

                    .setContentTitle(remoteMessage.getNotification().getTitle())

                    .setContentText(remoteMessage.getNotification().getBody())
                    .setColor(Color.parseColor("#FFD81B60"))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }

    }
}
