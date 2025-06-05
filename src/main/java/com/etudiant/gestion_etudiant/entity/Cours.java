package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String categorie;
    private LocalDate dateCreation;

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private User enseignant;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Support> supports;

    // ✅ Ajout relation OneToOne avec Quiz
    @OneToOne(mappedBy = "cours", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Quiz quiz;

    // Constructeurs
    public Cours() {}

    public Cours(String titre, String description, String categorie, LocalDate dateCreation, User enseignant) {
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
        this.enseignant = enseignant;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(User enseignant) {
        this.enseignant = enseignant;
    }

    public List<Support> getSupports() {
        return supports;
    }

    public void setSupports(List<Support> supports) {
        this.supports = supports;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
