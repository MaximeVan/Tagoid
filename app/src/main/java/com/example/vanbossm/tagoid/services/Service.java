package com.example.vanbossm.tagoid.services;

import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.data.Ligne;
import com.example.vanbossm.tagoid.data.Stoptime;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Service {
    @GET("api/routers/default/index/routes/")
    Call<Ligne[]> getLignes();

    @GET("api/routers/default/index/routes/{ligneID}/clusters")
    Call<Arret[]> getArrets(@Path(value = "ligneID", encoded = true) String ligneID);

    @GET("api/routers/default/index/clusters/{arretID}/stoptimes")
    Call<Stoptime[]> getStoptimes(@Path(value = "arretID", encoded = true) String arretID);
}
