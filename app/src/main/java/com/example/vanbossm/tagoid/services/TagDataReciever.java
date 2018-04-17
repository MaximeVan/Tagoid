package com.example.vanbossm.tagoid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TagDataReciever extends BroadcastReceiver {

    public TagDataReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RECIEVER", "Intent recieved");
    }
}
