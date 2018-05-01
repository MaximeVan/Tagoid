package com.example.vanbossm.tagoid;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.services.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service_Arrets extends IntentService {

    public Service_Arrets() {
        super("MyServiceArret");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Bundle bundle = intent.getExtras();
        String idLigne = (String) bundle.getSerializable("ligne");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://data.metromobilite.fr/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service arretsService = retrofit.create(Service.class);
        arretsService.getArrets(idLigne).enqueue(new Callback<Arret[]>() {
            @Override
            public void onResponse(@NonNull Call<Arret[]> call, @NonNull Response<Arret[]> response) {
                if(response.isSuccessful()){
                    Log.e("MY_SERVICE_ARRETS","Reponse recue.");

                    Intent intentToSend = new Intent(Constants.RECUPERATION_ARRETS)
                            .putExtra("Arrets", response.body());

                    Log.e("MY_SERVICE_ARRETS","Envoi en broadcast.");
                    LocalBroadcastManager.getInstance(Service_Arrets.this).sendBroadcast(intentToSend);
                } else {
                    Log.e("MY_SERVICE_ARRETS","Error response, no access to ressources :"+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Arret[]> call, @NonNull Throwable t) {
                Log.e("MY_SERVICE_ARRETS","Failure.",t);
            }
        });
    }
}
