package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Question;
import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.entity.Reponse;
import com.etudiant.gestion_etudiant.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // ✅ Récupérer un quiz par cours
    public Optional<Quiz> getQuizByCours(Cours cours) {
        return quizRepository.findByCours(cours);
    }

    @Transactional
    public void supprimerQuiz(Long quizId) {
        Quiz quiz = getQuizById(quizId);
        Cours cours = quiz.getCours();

        // Dissocier le quiz du cours
        if (cours != null) {
            cours.setQuiz(null);
        }

        // Supprimer les réponses
        List<Question> questions = questionRepository.findByQuizId(quizId);
        for (Question q : questions) {
            reponseRepository.deleteByQuestionId(q.getId());
        }

        // Supprimer les questions
        questionRepository.deleteByQuizId(quizId);

        // Supprimer le quiz
        quizRepository.deleteById(quizId);
    }

}
