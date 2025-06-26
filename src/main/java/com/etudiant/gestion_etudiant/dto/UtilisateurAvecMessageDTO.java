package com.etudiant.gestion_etudiant.dto;

import com.etudiant.gestion_etudiant.entity.User;

public class UtilisateurAvecMessageDTO {

    private User user;
    private String dernierMessage;

    // ✅ Constructeur
    public UtilisateurAvecMessageDTO(User user, String dernierMessage) {
        this.user = user;
        this.dernierMessage = dernierMessage;
    }

    // ✅ Getters
    public User getUser() {
        return user;
    }

    public String getDernierMessage() {
        return dernierMessage;
    }

    // ✅ Setters (optionnels si tu veux rendre la classe modifiable)
    public void setUser(User user) {
        this.user = user;
    }

    public void setDernierMessage(String dernierMessage) {
        this.dernierMessage = dernierMessage;
    }
}
