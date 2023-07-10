package com.tools.payhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.tools.payhelper.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;


public class DaemonService extends Service {

    public static final int NOTICE_ID = 0x64;

    private Timer timer;

    private AlarmReceiver alarmReceiver;

    @Nullable
    public IBinder onBind(Intent arg2) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Notification notification;
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("收款助手");
        builder.setContentText("收款助手正在运行中...");
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        notification = builder.build();
        startForeground(NOTICE_ID, notification);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.alarmReceiver);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(0x64);
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    public int onStartCommand(Intent arg2, int arg3, int arg4) {
        this.alarmReceiver = new AlarmReceiver();
        IntentFilter v0 = new IntentFilter();
        v0.addAction(Constants.NOTIFY_ACTION);
        registerReceiver(this.alarmReceiver, v0);

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction(Constants.NOTIFY_ACTION);
                sendBroadcast(intent);
            }
        };
        timer.schedule(timerTask, 30 * 1000, 60 * 1000);
        return Service.START_STICKY;
    }
}

