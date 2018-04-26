package com.example.vanbossm.tagoid;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.data.Ligne;
import com.example.vanbossm.tagoid.services.LignesService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService extends IntentService {

    public MyService() {
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

        LignesService lignesService = retrofit.create(LignesService.class);
        lignesService.getLignes().enqueue(new Callback<Ligne[]>() {
            @Override
            public void onResponse(@NonNull Call<Ligne[]> call, @NonNull Response<Ligne[]> response) {
                if(response.isSuccessful()){
                    Log.e("MY_SERVICE","Reponse recue !!");

                    Intent intentToSend = new Intent("RecuperationLignes");
                    intentToSend.putExtra("Lignes", response.body());

                    LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(intentToSend);
                } else {
                    // error response, no access to resource ?
                    Log.e("MY_SERVICE","Error response :"+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ligne[]> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Log.e("MY_SERVICE","Failure.",t);
            }
        });
    }
}
