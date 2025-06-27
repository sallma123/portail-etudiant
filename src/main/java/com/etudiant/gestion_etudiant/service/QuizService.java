package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private ReponseRepository reponseRepository;
    @Autowired private ResultatRepository resultatRepository;
    @Autowired private InscriptionRepository inscriptionRepository;

    // ✅ Création d’un quiz
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

    public Optional<Quiz> getQuizByCours(Cours cours) {
        return quizRepository.findByCours(cours);
    }

    // ✅ Suppression complète d’un quiz avec toutes ses dépendances
    @Transactional
    public void supprimerQuiz(Long quizId) {
        Quiz quiz = getQuizById(quizId);
        Cours cours = quiz.getCours();

        // ❌ Supprimer les résultats liés à ce quiz
        List<Resultat> resultats = resultatRepository.findByQuiz(quiz);
        resultatRepository.deleteAll(resultats);

        // ❌ Supprimer toutes les réponses liées à chaque question
        List<Question> questions = questionRepository.findByQuizId(quizId);
        for (Question q : questions) {
            reponseRepository.deleteByQuestionId(q.getId());
        }

        // ❌ Supprimer les questions du quiz
        questionRepository.deleteByQuizId(quizId);

        // ❌ Dissocier le quiz du cours s’il est lié
        if (cours != null) {
            cours.setQuiz(null);
        }

        // ✅ Supprimer enfin le quiz
        quizRepository.deleteById(quizId);
    }

    public void enregistrer(Quiz quiz) {
        quizRepository.save(quiz);
    }

    // ✅ Enregistrer la note de l'étudiant
    public void enregistrerResultatEtudiant(User etudiant, Cours cours, double note, int seuil) {
        Optional<Inscription> opt = inscriptionRepository.findByEtudiantAndCours(etudiant, cours);
        if (opt.isPresent()) {
            Inscription inscription = opt.get();
            Double ancienneNote = inscription.getNote();

            if (ancienneNote == null || note > ancienneNote) {
                inscription.setNote(note);
                inscription.setCertificatObtenu(note >= seuil);
                inscriptionRepository.save(inscription);
            }
        } else {
            // Nouvelle inscription avec note et certificat
            Inscription nouvelle = new Inscription();
            nouvelle.setEtudiant(etudiant);
            nouvelle.setCours(cours);
            nouvelle.setDateInscription(LocalDate.now());
            nouvelle.setNote(note);
            nouvelle.setCertificatObtenu(note >= seuil);
            nouvelle.setProgression(100);
            inscriptionRepository.save(nouvelle);
        }
    }
}
