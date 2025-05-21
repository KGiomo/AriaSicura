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
            description = "Qualità dell'aria sconosciuta.";
        } else if (aqi <= 50) {
            description = "Qualità dell'aria buona! Respira a pieni polmoni.";
        } else if (aqi <= 100) {
            description = "Qualità dell'aria moderata. Presta attenzione.";
        } else {
            description = "Qualità dell'aria scarsa! Evita le attività all'aperto.";
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("leo", "air notifications", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "leo")
                .setSmallIcon(R.mipmap.ic_launcher_round) // Logo dell'app
                .setContentTitle("Aria Sicura")
                .setContentText("AQI: " + (aqi == -1 ? "N/A" : aqi) + " - " + description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1, builder.build());
    }
}
