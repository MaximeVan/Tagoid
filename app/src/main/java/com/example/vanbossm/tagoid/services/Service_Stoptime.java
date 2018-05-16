package com.example.vanbossm.tagoid.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.Constants;
import com.example.vanbossm.tagoid.data.Stoptime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service_Stoptime extends IntentService {

    public Service_Stoptime() {
        super("MyServiceStoptime");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getExtras();
        String idArret = (String) bundle.getSerializable("arret");
        final String shortNameLigne = (String) bundle.getSerializable("ligne");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://data.metromobilite.fr/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        final Service stoptimeService = retrofit.create(Service.class);
        stoptimeService.getStoptimes(idArret).enqueue(new Callback<Stoptime[]>() {
            @Override
            public void onResponse(@NonNull Call<Stoptime[]> call, @NonNull Response<Stoptime[]> response) {
                if(response.isSuccessful()){
                    Log.e("MY_SERVICE_STOPTIME","Reponse recue.");

                    Intent intentToSend = new Intent(Constants.RECUPERATION_STOPTIMES)
                            .putExtra("Stoptimes", response.body());

                    Log.e("MY_SERVICE_STOPTIME","Envoi en broadcast.");
                    LocalBroadcastManager.getInstance(Service_Stoptime.this).sendBroadcast(intentToSend);
                } else {
                    Log.e("MY_SERVICE_STOPTIME","Error response, no access to ressources :"+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Stoptime[]> call, @NonNull Throwable t) {
                Log.e("MY_SERVICE_STOPTIME","Failure.",t);
            }
        });
    }
}
