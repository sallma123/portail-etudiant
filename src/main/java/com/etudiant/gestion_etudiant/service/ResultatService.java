package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResultatService {

    @Autowired private ResultatRepository resultatRepository;
    @Autowired private ReponseRepository reponseRepository;
    @Autowired private QuizRepository quizRepository;

    public double corrigerEtEnregistrer(User etudiant, Long quizId, Map<Long, List<Long>> reponsesEtudiant) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

        int totalQuestions = quiz.getQuestions().size();
        int bonnesReponses = 0;

        for (Question q : quiz.getQuestions()) {
            List<Reponse> bonnes = q.getReponses().stream()
                    .filter(Reponse::isCorrecte).collect(Collectors.toList());

            List<Long> reponsesIds = reponsesEtudiant.get(q.getId());
            if (reponsesIds == null) continue;

            List<Reponse> reponsesChoisies = reponseRepository.findAllById(reponsesIds);

            if (new java.util.HashSet<>(reponsesChoisies).equals(new java.util.HashSet<>(bonnes))) {
                bonnesReponses++;
            }
        }

        double note = (bonnesReponses * 100.0) / totalQuestions;

        Resultat resultat = new Resultat();
        resultat.setEtudiant(etudiant);
        resultat.setQuiz(quiz);
        resultat.setNote(note);
        resultat.setDate(LocalDate.now());

        resultatRepository.save(resultat);

        return note;
    }

    public Resultat getResultat(User etudiant, Quiz quiz) {
        return resultatRepository.findByEtudiantAndQuiz(etudiant, quiz).orElse(null);
    }
}
