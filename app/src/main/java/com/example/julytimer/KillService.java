package com.example.julytimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class KillService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onTaskRemoved(Intent rootIntent) {
        MainActivity c = new MainActivity();
        removeNotification();
        c.removeNotification();
        //stop service
        stopSelf();
    }

    public void removeNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel("35", "channel", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string.notification_name_1));
        notificationManager.createNotificationChannel(channel);
        notificationManager.cancel(1);
        notificationManager.cancel(3);
    }
}
