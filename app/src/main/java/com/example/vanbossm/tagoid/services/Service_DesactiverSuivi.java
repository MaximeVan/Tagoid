package com.example.vanbossm.tagoid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.Constants;

public class Service_DesactiverSuivi extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentToSend = new Intent(Constants.DESACTIVER_SUIVI);

        Log.e("MY_SERVICE_SUIVI","Envoi en broadcast.");
        LocalBroadcastManager.getInstance(Service_DesactiverSuivi.this).sendBroadcast(intentToSend);
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }
}
