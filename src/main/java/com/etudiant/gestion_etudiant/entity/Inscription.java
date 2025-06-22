package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User etudiant;

    @ManyToOne
    private Cours cours;

    private LocalDate dateInscription;

    private boolean termine;

    private int progression;

    private Double note;  // ✅ Ajouté : note de l'étudiant dans ce cours

    private boolean certificatObtenu;  // ✅ Ajouté : si le certificat a été obtenu

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public boolean isTermine() {
        return termine;
    }

    public void setTermine(boolean termine) {
        this.termine = termine;
    }

    public int getProgression() {
        return progression;
    }

    public void setProgression(int progression) {
        this.progression = progression;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public boolean isCertificatObtenu() {
        return certificatObtenu;
    }

    public void setCertificatObtenu(boolean certificatObtenu) {
        this.certificatObtenu = certificatObtenu;
    }

}
