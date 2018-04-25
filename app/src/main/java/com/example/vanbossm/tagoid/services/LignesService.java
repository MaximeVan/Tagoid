package com.example.vanbossm.tagoid.services;

import com.example.vanbossm.tagoid.data.Ligne;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LignesService {
    @GET("api/routers/default/index/routes/")
    Call<Ligne> getLignes();
}
