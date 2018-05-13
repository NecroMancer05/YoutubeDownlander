package com.bekircan.youtubedownloader;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class downloaderService extends Service {

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
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //notification
        Notification notification = new NotificationCompat.Builder(this)
                .setContentText("Running")
                .setContentTitle("Downloader Service")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_arrow_downward_black_24dp)
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
