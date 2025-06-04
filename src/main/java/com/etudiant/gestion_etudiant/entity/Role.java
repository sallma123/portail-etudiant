package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // ✅ Constructeur vide requis par JPA
    public Role() {}

    // ✅ Constructeur utilisé dans DataInitializer
    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
