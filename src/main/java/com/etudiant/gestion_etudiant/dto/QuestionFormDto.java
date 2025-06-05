package com.etudiant.gestion_etudiant.dto;

import java.util.List;

public class QuestionFormDto {

    private String question;

    private List<String> choix; // Liste des réponses proposées

    private List<Integer> corrects; // Indexes des bonnes réponses (ex : [1, 3])

    // Getters & setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getChoix() {
        return choix;
    }

    public void setChoix(List<String> choix) {
        this.choix = choix;
    }

    public List<Integer> getCorrects() {
        return corrects;
    }

    public void setCorrects(List<Integer> corrects) {
        this.corrects = corrects;
    }
}
