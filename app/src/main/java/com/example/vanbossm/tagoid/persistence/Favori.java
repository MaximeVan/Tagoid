package com.example.vanbossm.tagoid.persistence;

import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.data.Ligne;

public class Favori {

    private Ligne ligne;
    private Arret arret;

    public Favori(Ligne ligne, Arret arret) {
        this.ligne = ligne;
        this.arret = arret;
    }

    public Ligne getLigne() {
        return this.ligne;
    }

    public Arret getArret() {
        return  this.arret;
    }
}
