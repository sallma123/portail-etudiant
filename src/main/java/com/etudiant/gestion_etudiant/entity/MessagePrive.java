package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MessagePrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;

    private LocalDateTime date;

    @Column(nullable = false)
    private boolean lu = false;

    @ManyToOne
    private User expediteur;

    @ManyToOne
    private User destinataire;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public User getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(User expediteur) {
        this.expediteur = expediteur;
    }

    public User getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(User destinataire) {
        this.destinataire = destinataire;
    }
}
