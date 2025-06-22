package com.etudiant.gestion_etudiant.dto;

public class StatistiqueCours {

    private String nomCours;
    private int nbEtudiants;
    private int nbCertificats;
    private String moyenne;
    private int tauxReussite;
    private int nbCommentaires;

    // ✅ Constructeur complet
    public StatistiqueCours(String nomCours, int nbEtudiants, int nbCertificats, String moyenne, int tauxReussite, int nbCommentaires) {
        this.nomCours = nomCours;
        this.nbEtudiants = nbEtudiants;
        this.nbCertificats = nbCertificats;
        this.moyenne = moyenne;
        this.tauxReussite = tauxReussite;
        this.nbCommentaires = nbCommentaires;
    }

    // ✅ Getters
    public String getNomCours() {
        return nomCours;
    }

    public int getNbEtudiants() {
        return nbEtudiants;
    }

    public int getNbCertificats() {
        return nbCertificats;
    }

    public String getMoyenne() {
        return moyenne;
    }

    public int getTauxReussite() {
        return tauxReussite;
    }

    public int getNbCommentaires() {
        return nbCommentaires;
    }

    // (facultatif) toString(), equals(), hashCode() si besoin
}
