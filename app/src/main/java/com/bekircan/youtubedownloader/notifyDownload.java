package com.bekircan.youtubedownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static com.bekircan.youtubedownloader.notificationChannel.CHANNEL_ID;


public class notifyDownload {

    private downloadItem downloadItem;
    private Context context;
    private boolean notifyOn;

    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private Notification notification;

    private static final String NOTIFICATION_GROUP = "Group";


    public notifyDownload(com.bekircan.youtubedownloader.downloadItem downloadItem, Context context, boolean notifyOn) {
        this.downloadItem = downloadItem;
        this.context = context;
        this.notifyOn = notifyOn;
    }


    //useless method
    public void createNotify(){

        //Intent notificationIntent = new Intent(context, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /*
        builder = new NotificationCompat.Builder(context);
        //builder.setContentText(downloadItem.getFileName());
        builder.setContentTitle("Downloading");
        builder.setContentIntent(pendingIntent);
        builder.setColor(Color.argb(255, 0, 187, 211));
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_arrow_downward_white);
        */

        /*
        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_arrow_downward_white)
                .setContentTitle("Download Finished")
                .setContentText(downloadItem.getFileName())
                .setContentIntent(pendingIntent)
                .setColor(Color.argb(255, 0, 187, 211))
                .setAutoCancel(true)
                .build();


         */

    }


    //TODO finish not working -- need rework almost gone (not necessary)
    public void updateNotify(){

        builder.setContentText(downloadItem.getFileName());

        Log.d("notification", "update" + downloadItem.getDownStatus());

        /*
        if ((int) downloadItem.getDownStatus() != 100){

            builder.setProgress(100, (int) downloadItem.getDownStatus(), false);
            //builder.setProgress(0, 0, false);
            //builder.setContentText("Finished the download");
            //manager.notify(downloadItem.getId(), builder.build());
            Log.d("notification", "finish");
        }else {
            builder.setProgress(0, 0, false);
            builder.setContentTitle("Downloaded");
            //builder.setProgress(100, (int) downloadItem.getDownStatus(), false);
        }

        manager.notify(downloadItem.getId(), builder.build());
        */
    }


    public void finishNotify(){

        //builder.setProgress(0, 0, false);
        //builder.setContentTitle("Finished the download");
        //manager.notify(downloadItem.getId(), builder.build());



        Intent notificationIntent = new Intent(context, MainActivity.class);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);


        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_arrow_downward_white)
                .setContentTitle("Download Finished")
                .setContentText(downloadItem.getFileName())
                .setContentIntent(pendingIntent)
                .setColor(Color.argb(255, 0, 187, 211))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                //.setGroup(NOTIFICATION_GROUP) TODO maybe later group the notifications
                .build();

        manager.notify(downloadItem.getId(), notification);
        Log.d("notification", "finish" + downloadItem.getDownStatus());
    }


}
