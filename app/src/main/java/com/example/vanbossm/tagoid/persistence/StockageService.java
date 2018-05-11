package com.example.vanbossm.tagoid.persistence;

import android.content.Context;

import java.util.List;

public interface StockageService {
    /**
     * Enregistre la liste des articles passés en paramètre.
     * @param context contexte de l'activité
     * @param favoris liste des articles
     * @return liste des favoris sauvegardés par ordre alphabétique
     **/
    public List<Favori> store(Context context, List<Favori> favoris);

    /**
     * Récupère la liste des articles sauvegardés.
     * @param context contexte de l'activité
     * @return liste des favoris sauvegardés par ordre alphabétique
     */
    public List<Favori> restore(Context context);

    /**
     * Vide la liste des articles.
     * @param context contexte de l'activité
     * @return liste des favoris vide.
     */
    public List<Favori> clear(Context context);

    /**
     * Enregistre un nouvel article passé en paramètre.
     * @param context contexte de l'activité
     * @param favori favori
     */
    public void add(Context context, Favori favori);
}
