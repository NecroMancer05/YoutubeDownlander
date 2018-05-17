package com.bekircan.youtubedownloader;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import static com.bekircan.youtubedownloader.notificationChannel.CHANNEL_ID;

public class downloaderService extends Service {

    private static final String STOP_FOREGROUND_SERVICE = "Stop Service";

    //public static ArrayList<downloadItem> downloadItems = new ArrayList<>();
    //public static int indexCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //open app when notification click
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //stop service
        Intent stopForeground = new Intent(this, notificationReceiver.class);
        //stopForeground.setAction(STOP_FOREGROUND_SERVICE);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 1, stopForeground, PendingIntent.FLAG_UPDATE_CURRENT);


        //TODO add stop action to notification
        //notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Running")
                .setContentTitle("Downloader Service")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_arrow_downward_white)
                .setColor(Color.argb(255, 0, 187, 211))
                .addAction(R.drawable.ic_arrow_downward_white, STOP_FOREGROUND_SERVICE, pendingIntentStop)
                .build();



        //start with notification in foreground
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
