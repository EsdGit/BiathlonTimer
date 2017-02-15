package com.esd.esd.biathlontimer.Activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.esd.esd.biathlontimer.R;


public class TestService extends Service
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(777, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        if(Build.VERSION.SDK_INT == 19)
        {
            Intent restartIntent = new Intent(this, getClass());
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            //restartIntent.putExtra("RESTART");
            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
            startService(restartIntent);
        }
    }
}
