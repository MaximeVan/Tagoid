package com.example.vanbossm.tagoid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.Constants;

public class TagDataProvider extends IntentService {

    public TagDataProvider() {
        super("TagDataProvider");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = new Intent(Constants.BROADCAST_ACTION);

        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() < time + 2000) {
            Log.e("PROVIDER", "Still waiting...");
        }

        Log.e("PROVIDER", "Sending broadcast !");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        Log.e("PROVIDER", "Broadcast sent.");
    }
}
