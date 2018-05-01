package com.example.vanbossm.tagoid.services;

import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.data.Ligne;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Service {
    @GET("api/routers/default/index/routes/")
    Call<Ligne[]> getLignes();

    @GET("api/routers/default/index/routes/{name}/clusters")
    Call<Arret[]> getArrets(@Path(value = "name", encoded = true) String name);
}
