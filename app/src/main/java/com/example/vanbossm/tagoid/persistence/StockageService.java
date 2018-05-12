package com.example.vanbossm.tagoid.persistence;

import android.content.Context;

import java.util.List;

public interface StockageService {
    /**
     * Enregistre la liste des favoris passés en paramètre.
     * @param context contexte de l'activité
     * @param favoris liste des favoris
     **/
    void store(Context context, List<Favori> favoris);

    /**
     * Récupère la liste des favoris sauvegardés.
     * @param context contexte de l'activité
     * @return liste des favoris sauvegardés par ordre alphabétique
     */
    List<Favori> restore(Context context);

    /**
     * Vide la liste des favoris.
     * @param context contexte de l'activité
     * @return liste des favoris vide.
     */
    List<Favori> clear(Context context);

    /**
     * Enregistre un nouveau favori passé en paramètre.
     * @param context contexte de l'activité
     * @param favori favori
     */
    void add(Context context, Favori favori);

    /**
     * Supprime un item passé en paramètre.
     * @param context contexte de l'activité
     * @param key cle
     */
    void remove(Context context, String key);
}
