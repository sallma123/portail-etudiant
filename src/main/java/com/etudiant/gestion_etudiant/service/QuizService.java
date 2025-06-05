package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Question;
import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.entity.Reponse;
import com.etudiant.gestion_etudiant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private ReponseRepository reponseRepository;

    public Quiz ajouterQuiz(String titre, int seuil, Cours cours) {
        Quiz quiz = new Quiz();
        quiz.setTitre(titre);
        quiz.setSeuil(seuil);
        quiz.setCours(cours);
        return quizRepository.save(quiz);
    }

    public Question ajouterQuestion(String texte, Quiz quiz) {
        Question q = new Question();
        q.setTexte(texte);
        q.setQuiz(quiz);
        return questionRepository.save(q);
    }

    public Reponse ajouterReponse(String texte, boolean correcte, Question question) {
        Reponse r = new Reponse();
        r.setTexte(texte);
        r.setCorrecte(correcte);
        r.setQuestion(question);
        return reponseRepository.save(r);
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElseThrow();
    }
}
