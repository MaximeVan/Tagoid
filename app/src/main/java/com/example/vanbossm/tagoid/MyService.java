package com.example.vanbossm.tagoid;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.vanbossm.tagoid.data.Ligne;
import com.example.vanbossm.tagoid.services.LignesService;

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
    protected void onHandleIntent(@Nullable Intent intent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://data.metromobilite.fr/api/routers/default/index/routes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LignesService lignesService = retrofit.create(LignesService.class);
        lignesService.getLignes().enqueue(new Callback<Ligne>() {

            @Override
            public void onResponse(Call<Ligne> call, Response<Ligne> response) {
                if(response.isSuccessful()){
                    Ligne answser = response.body();

                    Intent intentToSend = new Intent(Constants.ACTION_DONE);
                    intentToSend.putExtra(Constants.EXTRA_ANSWER,answser.getId());

                    LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(intentToSend);
                } else {
                    // error response, no access to resource ?
                    Log.e("MyService","Error response :"+response.message());
                }
            }

            @Override
            public void onFailure(Call<Ligne> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.e("PROVIDER","Failure",t);
            }
        });
    }
}
