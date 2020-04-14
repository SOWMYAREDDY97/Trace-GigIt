package com.traceandgigit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class Utils {



    public static void showNotification(Context context, String title, String message,Intent notificationIntent) {

        long when = Calendar.getInstance().getTimeInMillis();
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setData(Uri.parse("content://" + when));
        notificationIntent.putExtra("title",title);
        notificationIntent.putExtra("message",message);
        /*create new task for each notification with pending intent so we set Intent.FLAG_ACTIVITY_NEW_TASK */
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

// define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    context.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Trace and GiGit Notifications");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        builder.setContentIntent(contentIntent)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(context.getResources().getString(R.string.app_name))
                .setSound(soundUri)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setWhen(when)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setColor(context.getResources()
                        .getColor(R.color.colorPrimary));

        Notification notification;

        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification();
            notification.defaults |= Notification.DEFAULT_ALL;
        } else {
            notification = builder.build();
            notification.priority = Notification.PRIORITY_MAX;
        }
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

                if (smallIconViewId != 0) {
                    if (notification.contentIntent != null)
                        notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                    if (notification.headsUpContentView != null)
                        notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                    if (notification.bigContentView != null)
                        notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.notify((int) when, notification);
        }
    }
}
