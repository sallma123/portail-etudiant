package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime date;

    private boolean vue; // true si l'utilisateur l'a déjà consultée

    @ManyToOne
    private User destinataire;

    // Getters & setters
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public boolean isVue() { return vue; }
    public void setVue(boolean vue) { this.vue = vue; }

    public User getDestinataire() { return destinataire; }
    public void setDestinataire(User destinataire) { this.destinataire = destinataire; }
}
