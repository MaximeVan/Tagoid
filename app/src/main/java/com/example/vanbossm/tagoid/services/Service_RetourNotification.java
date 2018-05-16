package com.example.vanbossm.tagoid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.Constants;

public class Service_RetourNotification extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentToSend = new Intent(Constants.RETOUR_NOTIFICATION);
        intentToSend.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        Log.e("MY_SERVICE_NOTIFICATION","Envoi en broadcast.");
        LocalBroadcastManager.getInstance(Service_RetourNotification.this).sendBroadcast(intentToSend);
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }
}
