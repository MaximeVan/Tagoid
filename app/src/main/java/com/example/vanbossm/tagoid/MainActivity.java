package com.example.vanbossm.tagoid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.data.Ligne;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static int NOTIFICATION_ID = 1;
    private Context context;
    private List<Ligne> lignesTram;
    private List<Ligne> lignesBus;
    private List<Arret> arrets;
    private String radioButtonChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayNone();

        context = this;
        radioButtonChecked = "";
        lignesBus = new ArrayList<Ligne>();
        lignesTram = new ArrayList<Ligne>();
        arrets = new ArrayList<Arret>();

        /*
        ============================================================================================
                                        Creation des Listeners
        ============================================================================================
         */
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                fillSpinnerLignes(checkedId);
            }
        });

        final Spinner spinnerLignes = (Spinner) findViewById(R.id.spinnerLignes);
        spinnerLignes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(radioButtonChecked.equals("TRAM")) {
                    for (Ligne ligne : lignesTram) {
                        if (ligne.getShortName().equals(spinnerLignes.getSelectedItem().toString())) {
                            spinnerLignes.setBackgroundColor(Color.parseColor("#" + ligne.getColor()));
                            break;
                        }
                    }
                }

                if(radioButtonChecked.equals("BUS")) {
                    for (Ligne ligne : lignesBus) {
                        if (ligne.getShortName().equals(spinnerLignes.getSelectedItem().toString())) {
                            spinnerLignes.setBackgroundColor(Color.parseColor("#" + ligne.getColor()));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*
        ============================================================================================
        ============================================================================================
         */

        // Creation du broadcast receiver
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("RECEIVER", "Broadcast recu : " + intent.getAction());

                if(intent.getAction().equals(Constants.RECUPERATION_LIGNES)) {
                    // Recuperation des lignes
                    Ligne[] toutesLignes = (Ligne[]) intent.getSerializableExtra("Lignes");
                    trierLignes(toutesLignes);
                }

                if(intent.getAction().equals(Constants.RECUPERATION_ARRETS)) {
                    // Recuperation des arrets
                    Arret[] tousArrets = (Arret[]) intent.getSerializableExtra("Arrets");
                    trierArrets(tousArrets);
                }
            }
        };

        // IntentFilter des lignes
        IntentFilter intentFilterLignes = new IntentFilter(Constants.RECUPERATION_LIGNES);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilterLignes);

        // IntentFilter des arrets
        IntentFilter intentFilterArrets = new IntentFilter(Constants.RECUPERATION_ARRETS);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilterArrets);

        // Creation du service pour recuperer les lignes
        Intent serviceLigne = new Intent(this, MyService.class);
        Log.e("MAIN", "Demarrage du service lignes.");
        startService(serviceLigne);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void trierLignes(Ligne[] toutesLignes) {
        for (Ligne ligneCourante : toutesLignes) {
            if (ligneCourante.getMode().equals("TRAM")) {
                lignesTram.add(ligneCourante);
            } else {
                lignesBus.add(ligneCourante);
            }
        }

        // Tri par ordre alphabetique
        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                Ligne str1 = (Ligne) o1;
                Ligne str2 = (Ligne) o2;
                return str1.getShortName().compareTo(str2.getShortName());
            }
        };
        lignesTram.sort(comp);
        lignesBus.sort(comp);
    }

    private void trierArrets(Arret[] tousArrets) {
        for(Arret arretCourant : tousArrets) {
            arrets.add(arretCourant);
        }
        fillSpinnerArrets();
    }

    private void fillSpinnerLignes(int checkedId) {
        Spinner spinnerLignes = (Spinner) findViewById(R.id.spinnerLignes);
        List<String> nomsLignes = new ArrayList<>();

        if(checkedId == 2131558518) {
            Log.e("RADIO_BUTTON_BUS", "Radio button bus checked. Remplissage des spinner lignes...");
            this.radioButtonChecked = "BUS";
            for (Ligne ligneBus: lignesBus) {
                nomsLignes.add(ligneBus.getShortName());
            }
        } else {
            Log.e("RADIO_BUTTON_TRAM", "Radio button tram checked. Remplissage des spinner lignes...");
            this.radioButtonChecked = "TRAM";
            for (Ligne ligneTram: lignesTram) {
                nomsLignes.add(ligneTram.getShortName());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, nomsLignes);
        spinnerLignes.setAdapter(spinnerAdapter);

        displayLigneLayout();

        // Creation du service pour recuperer les lignes
        Intent serviceArret = new Intent(this, MyServiceArrets.class);
        Log.e("MAIN", "Demarrage du service arrets.");
        startService(serviceArret);
    }

    private void fillSpinnerArrets() {
        Spinner spinnerArrets = (Spinner) findViewById(R.id.spinnerArrets);
        List<String> nomsArrets = new ArrayList<>();

        Log.e("SPINNER_ARRETS", "Ligne choisie. Remplissage des spinner arrets...");
        for (Arret arretCourant: arrets) {
            nomsArrets.add(arretCourant.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, nomsArrets);
        spinnerArrets.setAdapter(spinnerAdapter);

        displayLigneLayout();
        // fillListViewArrets(); TODO
    }


    /*
    ============================================================================================
                                            Notification
    ============================================================================================
     */
    public void sendNotification() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/"));
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

    /*
    ============================================================================================
    ============================================================================================
     */



    /*
    ============================================================================================
                                            Display
    ============================================================================================
     */
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
    /*
    ============================================================================================
    ============================================================================================
     */
}
