package com.bekircan.youtubedownloader;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class notifyDownload {

    private downloadItem downloadItem;
    private Context context;
    private boolean notifyOn;

    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    public notifyDownload(com.bekircan.youtubedownloader.downloadItem downloadItem, Context context, boolean notifyOn) {
        this.downloadItem = downloadItem;
        this.context = context;
        this.notifyOn = notifyOn;
    }


    public void cretateNotify(){

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(context);
        //builder.setContentText(downloadItem.getFileName());
        builder.setContentTitle("Downloading");
        builder.setSmallIcon(R.drawable.ic_arrow_downward_black_24dp);


    }


    //TODO finish not working -- need rework almost gone
    public void updateNotify(){

        builder.setContentText(downloadItem.getFileName());

        Log.d("notification", "update" + downloadItem.getDownStatus());

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
    }


    public void finishNotify(){

        builder.setProgress(0, 0, false);
        builder.setContentTitle("Finished the download" + downloadItem.getFileName());
        manager.notify(downloadItem.getId(), builder.build());
        Log.d("notification", "finish" + downloadItem.getDownStatus());
    }


}
