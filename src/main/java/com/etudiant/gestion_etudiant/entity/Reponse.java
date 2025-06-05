package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;

@Entity
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texte;

    private boolean correcte; // true si cette réponse est la bonne

    @ManyToOne
    private Question question;

    // Getters & setters
    public Long getId() { return id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public boolean isCorrecte() { return correcte; }
    public void setCorrecte(boolean correcte) { this.correcte = correcte; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}
