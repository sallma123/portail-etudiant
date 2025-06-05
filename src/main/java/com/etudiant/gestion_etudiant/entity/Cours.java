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
    // Constructors
    public Cours() {
    }

    public Cours(String titre, String description, String categorie, LocalDate dateCreation, User enseignant) {
        this.titre = titre;
        this.description = description;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
        this.enseignant = enseignant;
    }

    // Getters and Setters
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
}
