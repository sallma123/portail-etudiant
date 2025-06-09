package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MessageForum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime date;

    @ManyToOne
    private User auteur;

    @ManyToOne
    private Cours cours;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }
}
