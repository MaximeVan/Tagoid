package com.example.vanbossm.tagoid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TagDataReciever extends BroadcastReceiver {

    private String extra;

    private TagDataReciever() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.extra = intent.getStringExtra("Status");
    }

    public String getExtra() {
        return this.extra;
    }
}
