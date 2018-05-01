package com.example.vanbossm.tagoid;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.data.Ligne;
import com.example.vanbossm.tagoid.services.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service_Lignes extends IntentService {

    public Service_Lignes() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://data.metromobilite.fr/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service lignesService = retrofit.create(Service.class);
        lignesService.getLignes().enqueue(new Callback<Ligne[]>() {
            @Override
            public void onResponse(@NonNull Call<Ligne[]> call, @NonNull Response<Ligne[]> response) {
                if(response.isSuccessful()){
                    Log.e("MY_SERVICE_LIGNES","Reponse recue.");

                    Intent intentToSend = new Intent(Constants.RECUPERATION_LIGNES)
                            .putExtra("Lignes", response.body());

                    Log.e("MY_SERVICE_LIGNES","Envoi en broadcast.");
                    LocalBroadcastManager.getInstance(Service_Lignes.this).sendBroadcast(intentToSend);
                } else {
                    Log.e("MY_SERVICE_LIGNES","Error response, no access to ressources :"+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ligne[]> call, @NonNull Throwable t) {
                Log.e("MY_SERVICE_LIGNES","Failure.",t);
            }
        });
    }
}
