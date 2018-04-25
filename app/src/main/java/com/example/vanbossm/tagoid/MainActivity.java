package com.example.vanbossm.tagoid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.vanbossm.tagoid.data.Ligne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static int NOTIFICATION_ID = 1;
    private List<Ligne> lignesTram;
    private List<Ligne> lignesBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lignesBus = new ArrayList<Ligne>();
        lignesTram = new ArrayList<Ligne>();

        // Creation du broadcast receiver
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("RECEIVER", "Intent received, make a notification here");

                Ligne[] toutesLignes = (Ligne[]) intent.getSerializableExtra("Lignes");
                for (Ligne ligneCourante: toutesLignes) {
                    if(ligneCourante.getMode().equals("TRAM")) {
                        lignesTram.add(ligneCourante);
                    }
                    else {
                        lignesBus.add(ligneCourante);
                    }
                }

                Log.e("RECEIVER", "Intent : "+intent.getStringExtra("Lignes"));

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                .setContentTitle("Tagoid")
                                .setContentText("Pwit pwit")
                                .setContentIntent(pendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(001, mBuilder.build());
            }
        };

        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_DONE);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        // Creation du service pour recuperer les lignes
        Intent service = new Intent(this, MyService.class);
        Log.e("MAIN", "Demarrage du service.");
        startService(service);







        /*try {
            downloadUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
