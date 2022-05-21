package com.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    // Notification action elements
    private Intent mNotificationIntent;
    private PendingIntent mPendingIntent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mNotificationIntent = new Intent(context, MainActivity.class);
            mPendingIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            // Build notification
            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setTicker("Time for another selfie")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("Time to another selfie")
                    .setContentIntent(mPendingIntent);


            String channelId = "ALARM";
            NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Time to another selfie",
                        NotificationManager.IMPORTANCE_HIGH);

            // Get NotificationManager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            Toast.makeText(context, "Notification", Toast.LENGTH_LONG).show();
        }
        catch (Exception exception) {
            Log.d("NOTIFICATION", exception.getMessage().toString());
        }
    }

}
