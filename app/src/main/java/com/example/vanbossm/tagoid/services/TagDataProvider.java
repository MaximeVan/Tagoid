package com.example.vanbossm.tagoid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.vanbossm.tagoid.Constants;

public class TagDataProvider extends IntentService {

    public TagDataProvider() {
        super("TagDataProvider");

        System.out.println("ca passe par ici 2");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent broadcastIntent = new Intent(Constants.BROADCAST_ACTION)
                        .putExtra(Constants.EXTENDED_DATA_STATUS, "Status");

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }
}
