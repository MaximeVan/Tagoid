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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vanbossm.tagoid.data.Ligne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.vanbossm.tagoid.R.layout.support_simple_spinner_dropdown_item;

public class MainActivity extends AppCompatActivity{

    private static int NOTIFICATION_ID = 1;
    private Context context;
    private List<Ligne> lignesTram;
    private List<Ligne> lignesBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayNone();

        context = this;
        lignesBus = new ArrayList<Ligne>();
        lignesTram = new ArrayList<Ligne>();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                fillSpinnerLignes(checkedId);
            }
        });

        // Creation du broadcast receiver
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("RECEIVER", "Intent received : " + intent.getAction());

                // Recuperation des lignes
                if(intent.getAction().equals("RecuperationLignes")) {
                    Ligne[] toutesLignes = (Ligne[]) intent.getSerializableExtra("Lignes");
                    for (Ligne ligneCourante : toutesLignes) {
                        if (ligneCourante.getMode().equals("TRAM")) {
                            lignesTram.add(ligneCourante);
                        } else {
                            lignesBus.add(ligneCourante);
                        }
                    }
                }

                // Notification
                /*intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                .setContentTitle("Tagoid")
                                .setContentText("Pwit pwit")
                                .setContentIntent(pendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(001, mBuilder.build());*/
            }
        };

        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_DONE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        // Creation du service pour recuperer les lignes
        Intent service = new Intent(this, MyService.class);
        Log.e("MAIN", "Demarrage du service.");
        startService(service);
    }

    private void fillSpinnerLignes(int checkedId) {
        Spinner spinnerLignes = (Spinner) findViewById(R.id.spinnerLignes);
        List<String> nomsLignes = new ArrayList<>();

        if(checkedId == 2131558518) {
            Log.e("RADIO_BUTTON_BUS", "Radio button bus checked. Remplissage des spinner...");
            for (Ligne ligneBus: lignesBus) {
                nomsLignes.add(ligneBus.getLongName());
            }
        } else {
            Log.e("RADIO_BUTTON_TRAM", "Radio button tram checked. Remplissage des spinner...");
            for (Ligne ligneTram: lignesTram) {
                nomsLignes.add(ligneTram.getLongName());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, nomsLignes);
        spinnerLignes.setAdapter(spinnerAdapter);

        displayLigneLayout();
    }


    public void displayNone() {
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinnerLignes);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerArrets);
        ListView lv1 = (ListView) findViewById(R.id.listViewArrets);

        tv2.setVisibility(View.INVISIBLE);
        tv3.setVisibility(View.INVISIBLE);
        spinner1.setVisibility(View.INVISIBLE);
        spinner2.setVisibility(View.INVISIBLE);
        lv1.setVisibility(View.INVISIBLE);
    }

    public void displayLigneLayout() {
        TextView tv = (TextView) findViewById(R.id.textView2);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerLignes);

        tv.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }

    public void displayArretLayout() {
        TextView tv = (TextView) findViewById(R.id.textView3);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerArrets);

        tv.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }

    public void displayListeLayout() {
        ListView lv1 = (ListView) findViewById(R.id.listViewArrets);

        lv1.setVisibility(View.VISIBLE);
    }
}
