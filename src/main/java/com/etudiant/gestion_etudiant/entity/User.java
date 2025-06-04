package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nom;
    private String prenom;
    private boolean enabled = true;

    @ManyToOne
    private Role role;

    private String resetToken; // ✅ Ajout du token de réinitialisation

    @OneToMany(mappedBy = "enseignant", cascade = CascadeType.ALL)
    private List<Cours> coursEnseignes; // ✅ Ajout de la liste des cours enseignés

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public List<Cours> getCoursEnseignes() {
        return coursEnseignes;
    }

    public void setCoursEnseignes(List<Cours> coursEnseignes) {
        this.coursEnseignes = coursEnseignes;
    }
}
