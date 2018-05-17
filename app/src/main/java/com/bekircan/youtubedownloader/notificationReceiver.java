package com.bekircan.youtubedownloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class notificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        context.stopService(new Intent(context, com.bekircan.youtubedownloader.downloaderService.class));

    }

}
