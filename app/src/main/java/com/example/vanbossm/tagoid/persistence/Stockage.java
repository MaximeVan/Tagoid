package com.example.vanbossm.tagoid.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Stockage implements StockageService {
    @Override
    public void store(Context context, List<Favori> favoris) {
        SharedPreferences preferences = context.getSharedPreferences("preference", 0);
        Gson gson = new Gson();

        String value = gson.toJson(favoris);
        SharedPreferences.Editor editor = preferences.edit().putString("preferenceJSON", value);
        editor.apply();
    }

    @Override
    public List<Favori> restore(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference", 0);
        Gson gson = new Gson();

        String value = preferences.getString("preferenceJSON", "");
        return gson.fromJson(value, new TypeToken<ArrayList<Favori>>(){}.getType());
    }

    @Override
    public List<Favori> clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preference", 0);
        Gson gson = new Gson();

        String value = preferences.getString("preferenceJSON", "");
        List<Favori> favoris = gson.fromJson(value, new TypeToken<ArrayList<Favori>>(){}.getType());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        return favoris;
    }

    @Override
    public void add(Context context, Favori favori) {
        SharedPreferences preferences = context.getSharedPreferences("preference", 0);
        Gson gson = new Gson();

        String value = preferences.getString("preferenceJSON", "");
        List<Favori> favoris = gson.fromJson(value, new TypeToken<ArrayList<Favori>>(){}.getType());

        if(favoris == null) {
            favoris = new ArrayList<>();
        }
        favoris.add(favori);
        store(context, favoris);
    }

    public void remove(Context context, String key) {
        String nomLigne = key.split(" : ")[0].split(" ")[1];
        String nomArret = key.split(" : ")[1];

        SharedPreferences preferences = context.getSharedPreferences("preference", 0);
        Gson gson = new Gson();

        String value = preferences.getString("preferenceJSON", "");
        List<Favori> favoris = gson.fromJson(value, new TypeToken<ArrayList<Favori>>(){}.getType());

        for(int i = 0; i < favoris.size(); i++) {
            if(favoris.get(i).getLigne().getShortName().equals(nomLigne)
                    && favoris.get(i).getArret().getName().equals(nomArret)) {
                favoris.remove(i);
            }
        }

        clear(context);
        store(context, favoris);
    }
}
