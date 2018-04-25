package com.example.vanbossm.tagoid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creation du broadcast receiver
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("RECEIVER", "Intent received, make a notification here");
            }
        };

        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_DONE);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        Log.e("MAIN", "Done.");

        // Creation du service pour recuperer les lignes
        Intent service = new Intent(this, MyService.class);
        startService(service);







        /*try {
            downloadUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
