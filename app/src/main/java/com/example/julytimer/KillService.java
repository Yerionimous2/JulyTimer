package com.example.julytimer;

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
        c.removeNotification();
        System.out.println("Wird ausgeführt!");
        //stop service
        stopSelf();
    }
}
