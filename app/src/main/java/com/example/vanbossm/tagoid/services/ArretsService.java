package com.example.vanbossm.tagoid.services;

import com.example.vanbossm.tagoid.data.Arret;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ArretsService {
    @GET("api/routers/default/index/routes/")
    Call<Arret[]> getArrets(String ligne);
}
