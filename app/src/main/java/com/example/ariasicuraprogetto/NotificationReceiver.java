package com.example.ariasicuraprogetto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int aqi = prefs.getInt("last_aqi", -1);

        String description;
        if (aqi == -1) {
            description = "Unknown air quality.";
        } else if (aqi <= 50) {
            description = "Good air quality! Breathe hard.";
        } else if (aqi <= 100) {
            description = "Moderate air quality. Pay attention.";
        } else {
            description = "Poor air quality! Avoid outdoor activities.";
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("leo", "Notifiche Aria", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "leo")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Logo dell'app
                .setContentTitle("ECO-DIGIFY")
                .setContentText("AQI: " + (aqi == -1 ? "N/A" : aqi) + " - " + description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1, builder.build());
    }
}