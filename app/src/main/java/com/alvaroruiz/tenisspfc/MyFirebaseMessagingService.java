package com.alvaroruiz.tenisspfc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by alvaroruiz on 17/03/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

        String channelId = "Default";
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        //Cancel Notification
        if (remoteMessage.getData().get("page").equals("/remove-notification")) {
            notificationManager.cancel(Integer.parseInt(remoteMessage.getData().get("id")));
            return;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("body")));

        try {
            if (remoteMessage.getData().get("image_big") != null) {
                Bitmap bm = getBitmapFromURL(remoteMessage.getData().get("image_big"));
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bm)
                        .bigLargeIcon(null)
                        .setSummaryText(remoteMessage.getData().get("body")));
            }
            if (remoteMessage.getData().get("image_mini") != null) {
                Bitmap bm = getBitmapFromURL(remoteMessage.getData().get("image_mini"));
                notificationBuilder.setLargeIcon(bm);
            }


        } catch (IOException e) {

        }
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        int id = remoteMessage.getData().get("id") == null ? 0 : Integer.parseInt(remoteMessage.getData().get("id"));

        //Set Notificaiton Click
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_VIEW);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("PAGE", remoteMessage.getData().get("page"));
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(id, notificationBuilder.build());
        //notificationManager.notify(0, groupBuilder.build());


    }

    public static Bitmap getBitmapFromURL(String src) throws IOException {
        try {
            InputStream in = new java.net.URL(src).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}
