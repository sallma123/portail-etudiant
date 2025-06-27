package com.etudiant.gestion_etudiant.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) // 🔁 Résultat supprimé si l'étudiant est supprimé
    private User etudiant;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) // 🔁 Résultat supprimé si le quiz est supprimé
    private Quiz quiz;

    private double note;

    private LocalDate date;

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
