package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class SupportVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User etudiant;

    @ManyToOne
    private Support support;

    private LocalDate dateVue;

    // Getters et Setters
    public Long getId() { return id; }

    public User getEtudiant() { return etudiant; }
    public void setEtudiant(User etudiant) { this.etudiant = etudiant; }

    public Support getSupport() { return support; }
    public void setSupport(Support support) { this.support = support; }

    public LocalDate getDateVue() { return dateVue; }
    public void setDateVue(LocalDate dateVue) { this.dateVue = dateVue; }
}
