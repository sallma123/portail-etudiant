package com.etudiant.gestion_etudiant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;
    private String type;
    private String lien;

    @ManyToOne
    @JoinColumn(name = "cours_id")
    @JsonIgnore
    private Cours cours;

    // Constructors
    public Support() {
    }

    public Support(String nomFichier, String type, String lien, Cours cours) {
        this.nomFichier = nomFichier;
        this.type = type;
        this.lien = lien;
        this.cours = cours;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }
}
