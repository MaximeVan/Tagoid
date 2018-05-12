package com.example.vanbossm.tagoid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.data.Ligne;
import com.example.vanbossm.tagoid.data.Stoptime;
import com.example.vanbossm.tagoid.persistence.Favori;
import com.example.vanbossm.tagoid.persistence.Stockage;
import com.example.vanbossm.tagoid.persistence.StockageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    private static int NOTIFICATION_ID = 1;
    private Context context;
    private List<Ligne> lignesTram;
    private List<Ligne> lignesBus;
    private List<Arret> arrets;
    private List<Stoptime> stoptimes;
    private String radioButtonChecked;
    private Ligne selectedLine;
    private Arret selectedArret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayNone();

        context = this;
        radioButtonChecked = "";
        lignesBus = new ArrayList<>();
        lignesTram = new ArrayList<>();
        arrets = new ArrayList<>();
        stoptimes = new ArrayList<>();
        selectedLine = null;
        selectedArret = null;

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
                displayListeLayout(false);

                if(!spinnerLignes.getSelectedItem().toString().equals("...")) {
                    if (radioButtonChecked.equals("TRAM")) {
                        for (Ligne ligne : lignesTram) {
                            if (ligne.getShortName().equals(spinnerLignes.getSelectedItem().toString())) {
                                selectedLine = ligne;
                                break;
                            }
                        }
                    }

                    if (radioButtonChecked.equals("BUS")) {
                        for (Ligne ligne : lignesBus) {
                            if (ligne.getShortName().equals(spinnerLignes.getSelectedItem().toString())) {
                                selectedLine = ligne;
                                break;
                            }
                        }
                    }

                    // Creation du service pour recuperer les lignes
                    Intent serviceArret = new Intent(context, Service_Arrets.class);
                    serviceArret.putExtra("ligne", selectedLine.getId());
                    Log.e("MAIN", "Demarrage du service arrets.");
                    startService(serviceArret);
                } else {
                    spinnerLignes.setBackground(getResources().getDrawable(R.drawable.spinnerlayout));
                    displayListeLayout(false);
                    displayArretLayout(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Spinner spinnerArrets = (Spinner) findViewById(R.id.spinnerArrets);
        spinnerArrets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spinnerArrets.getSelectedItem().toString().equals("...")) {
                    for (Arret arret : arrets) {
                        if (arret.getName().equals(spinnerArrets.getSelectedItem().toString())) {
                            selectedArret = arret;
                            break;
                        }
                    }

                    // Creation du service pour recuperer les stoptimes
                    final Intent serviceStoptime = new Intent(context, Service_Stoptime.class);
                    serviceStoptime.putExtra("arret", selectedArret.getCode());
                    serviceStoptime.putExtra("ligne", selectedLine.getShortName());
                    Log.e("MAIN", "Demarrage du service stoptimes.");
                    startService(serviceStoptime);

                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if(!spinnerLignes.getSelectedItem().toString().equals("...") && !spinnerArrets.getSelectedItem().toString().equals("...")) {
                                Log.e("TIMER", "Rafraichissement des stoptimes.");
                                startService(serviceStoptime);
                            }
                        }
                    };
                    timer.schedule(timerTask, 0, 30000);
                } else {
                    displayListeLayout(false);
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
                    Log.e("RECEIVER", "Recuperation des lignes.");
                    Ligne[] toutesLignes = (Ligne[]) intent.getSerializableExtra("Lignes");
                    trierLignes(toutesLignes);
                }

                if(intent.getAction().equals(Constants.RECUPERATION_ARRETS)) {
                    // Recuperation des arrets
                    Log.e("RECEIVER", "Recuperation des arrets.");
                    Arret[] tousArrets = (Arret[]) intent.getSerializableExtra("Arrets");
                    trierArrets(tousArrets);
                }

                if(intent.getAction().equals(Constants.RECUPERATION_STOPTIMES)) {
                    // Recuperation des arrets
                    Log.e("RECEIVER", "Recuperation des stoptimes.");
                    Stoptime[] tousStoptimes = (Stoptime[]) intent.getSerializableExtra("Stoptimes");
                    trierStoptimes(tousStoptimes);
                }
            }
        };

        // IntentFilter des lignes
        IntentFilter intentFilterLignes = new IntentFilter(Constants.RECUPERATION_LIGNES);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilterLignes);

        // IntentFilter des arrets
        IntentFilter intentFilterArrets = new IntentFilter(Constants.RECUPERATION_ARRETS);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilterArrets);

        // IntentFilter des stoptimes
        IntentFilter intentFilterStoptimes = new IntentFilter(Constants.RECUPERATION_STOPTIMES);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilterStoptimes);

        // Creation du service pour recuperer les lignes
        Intent serviceLigne = new Intent(this, Service_Lignes.class);
        Log.e("MAIN", "Demarrage du service lignes.");
        startService(serviceLigne);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings_addAsFav:
                addAsFavorite();
                return true;
            case R.id.action_settings_favs:
                ouvrirFavoris();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ouvrirFavoris() {
        StockageService stockage = new Stockage();
        List<Favori> favoris = stockage.restore(this);
        if (favoris == null) {
            Toast.makeText(MainActivity.this, "Aucun favori.", Toast.LENGTH_SHORT).show();
        } else {
            Intent favorisActivity = new Intent(getApplicationContext(), FavorisActivity.class);
            startActivityForResult(favorisActivity, 1);
        }
    }

    public void addAsFavorite() {
        final Spinner spinnerArrets = (Spinner) findViewById(R.id.spinnerArrets);
        final Spinner spinnerLignes = (Spinner) findViewById(R.id.spinnerLignes);

        if(spinnerArrets.getSelectedItem() != null && !spinnerArrets.getSelectedItem().equals("...") && !spinnerLignes.getSelectedItem().equals("...")) {
            Favori newFavori = new Favori(selectedLine, selectedArret);

            StockageService stockage = new Stockage();
            stockage.add(getApplicationContext(), newFavori);

            Toast.makeText(this, "Ajout aux favori.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Veuillez sélectionner une ligne et un arrêt.", Toast.LENGTH_LONG).show();
        }
    }


    /*
    ============================================================================================
                                    Algorithmes de tri
    ============================================================================================
     */
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
        arrets.clear();

        for(Arret arretCourant : tousArrets) {
            arrets.add(arretCourant);
        }
        fillSpinnerArrets();
    }

    public void trierStoptimes(Stoptime[] tousStoptimes) {
        stoptimes.clear();
        String dir1 = "";
        String dir2 = "";

        for (Stoptime currentStoptime : tousStoptimes) {
            if(currentStoptime.getPattern().getId().split(":")[1].equals(selectedLine.getShortName())
                    && currentStoptime.getPattern().getId().split(":")[0].equals("SEM")){
                stoptimes.add(currentStoptime);

                if(currentStoptime.getPattern().getDir() == 1) {
                    dir1 = currentStoptime.getPattern().getDesc();
                } else {
                    dir2 = currentStoptime.getPattern().getDesc();
                }
            }
        }
        fillListViewArrets(dir1, dir2);
    }
    /*
    ============================================================================================
    ============================================================================================
    */

    public String convertirStoptime(Double time) {
        String currentDate = new Date().toString();
        String currentTime = currentDate.split(" ")[3];
        long currentHourInSec = Integer.parseInt(currentTime.split(":")[0])*3600;
        long currentMinutesInSec = Integer.parseInt(currentTime.split(":")[1])*60;
        long currentSec = Integer.parseInt(currentTime.split(":")[2]);
        long currentTimeInSec = currentHourInSec+currentMinutesInSec+currentSec;

        Double realTime = (time-currentTimeInSec)/60;
        String timeToReturn;

        if (realTime < 1) {
            timeToReturn = "< 1.0";
        } else {
            timeToReturn = Math.floor(realTime)+"";// + " min";
        }

        return timeToReturn.substring(0, timeToReturn.length()-2) + " min";
    }

    /*
    ============================================================================================
                                          Gestion IHM
    ============================================================================================
    */
    private void fillSpinnerLignes(int checkedId) {
        Spinner spinnerLignes = (Spinner) findViewById(R.id.spinnerLignes);
        List<String> nomsLignes = new ArrayList<>();
        nomsLignes.add("...");

        if(checkedId == R.id.radioButtonBus) {
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

        displayLigneLayout(true);
    }

    private void fillSpinnerArrets() {
        Spinner spinnerArrets = (Spinner) findViewById(R.id.spinnerArrets);
        List<String> nomsArrets = new ArrayList<>();
        nomsArrets.add("...");

        Log.e("SPINNER_ARRETS", "Ligne choisie. Remplissage des spinner arrets...");
        for (Arret arretCourant: arrets) {
            nomsArrets.add(arretCourant.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, nomsArrets);
        spinnerArrets.setAdapter(spinnerAdapter);

        displayArretLayout(true);
    }

    private void fillListViewArrets(String dir1, String dir2) {
        ListView listViewArrets1 = (ListView) findViewById(R.id.listViewArretsDir1);
        ListView listViewArrets2 = (ListView) findViewById(R.id.listViewArretsDir2);
        List<Double> timesDir1Double = new ArrayList<>();
        List<Double> timesDir2Double = new ArrayList<>();
        List<String> timesDir1ToReturn = new ArrayList<>();
        List<String> timesDir2ToReturn = new ArrayList<>();

        for(Stoptime currentStoptime : stoptimes) {
            if (currentStoptime.getPattern().getDir() == 1) {
                int index = 0;
                while(index < currentStoptime.getTimes().size()) {
                    if (currentStoptime.getTimes().get(index).getRealtimeDeparture() != null) {
                        timesDir1Double.add((Double) currentStoptime.getTimes().get(index).getRealtimeDeparture());
                    } else {
                        timesDir1Double.add(99999.0);
                    }
                    index++;
                }
            }
            if (currentStoptime.getPattern().getDir() == 2) {
                int index = 0;
                while(index < currentStoptime.getTimes().size()) {
                    if (currentStoptime.getTimes().get(index).getRealtimeDeparture() != null) {
                        timesDir2Double.add((Double) currentStoptime.getTimes().get(index).getRealtimeDeparture());
                    } else {
                        timesDir2Double.add(99999.0);
                    }
                    index++;
                }
            }
        }

        Collections.sort(timesDir1Double);
        Collections.sort(timesDir2Double);
        for(int i = 0; i < 2; i++) {
            if(timesDir1Double.get(i) != 99999.0) {
                timesDir1ToReturn.add(convertirStoptime(timesDir1Double.get(i)));
            } else {
                timesDir1ToReturn.add("--");
            }
            if(timesDir2Double.get(i) != 99999.0) {
                timesDir2ToReturn.add(convertirStoptime(timesDir2Double.get(i)));
            } else {
                timesDir2ToReturn.add("--");
            }
        }

        ArrayAdapter<String> listViewAdapter1 = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, timesDir1ToReturn);
        ArrayAdapter<String> listViewAdapter2 = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, timesDir2ToReturn);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        TextView textView5 = (TextView) findViewById(R.id.textView5);

        listViewArrets1.setAdapter(listViewAdapter1);
        listViewArrets2.setAdapter(listViewAdapter2);
        textView4.setText("Dir. "+dir1);
        textView5.setText("Dir. "+dir2);

        displayListeLayout(true);
    }
    /*
    ============================================================================================
    ============================================================================================
    */



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
        mNotificationManager.notify(1, mBuilder.build());
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
        TextView tv4 = (TextView) findViewById(R.id.textView4);
        TextView tv5 = (TextView) findViewById(R.id.textView5);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinnerLignes);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerArrets);
        ListView lv1 = (ListView) findViewById(R.id.listViewArretsDir1);
        ListView lv2 = (ListView) findViewById(R.id.listViewArretsDir2);

        tv2.setVisibility(View.INVISIBLE);
        tv3.setVisibility(View.INVISIBLE);
        tv4.setVisibility(View.INVISIBLE);
        tv5.setVisibility(View.INVISIBLE);
        spinner1.setVisibility(View.INVISIBLE);
        spinner2.setVisibility(View.INVISIBLE);
        lv1.setVisibility(View.INVISIBLE);
        lv2.setVisibility(View.INVISIBLE);
    }

    public void displayLigneLayout(boolean bool) {
        TextView tv = (TextView) findViewById(R.id.textView2);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerLignes);

        if(bool) {
            tv.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }
    }

    public void displayArretLayout(boolean bool) {
        TextView tv = (TextView) findViewById(R.id.textView3);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerArrets);

        if(bool) {
            tv.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }
    }

    public void displayListeLayout(boolean bool) {
        ListView lv1 = (ListView) findViewById(R.id.listViewArretsDir1);
        ListView lv2 = (ListView) findViewById(R.id.listViewArretsDir2);
        TextView tv4 = (TextView) findViewById(R.id.textView4);
        TextView tv5 = (TextView) findViewById(R.id.textView5);

        if(bool) {
            if(tv4.getText() != "") {
                lv1.setVisibility(View.VISIBLE);
                tv4.setVisibility(View.VISIBLE);
            }
            if(tv5.getText() != "") {
                lv2.setVisibility(View.VISIBLE);
                tv5.setVisibility(View.VISIBLE);
            }
        } else {
            lv1.setVisibility(View.INVISIBLE);
            lv2.setVisibility(View.INVISIBLE);
            tv4.setVisibility(View.INVISIBLE);
            tv5.setVisibility(View.INVISIBLE);
        }
    }
    /*
    ============================================================================================
    ============================================================================================
     */
}
