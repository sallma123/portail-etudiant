package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.entity.Resultat;
import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultatRepository extends JpaRepository<Resultat, Long> {

    Optional<Resultat> findTopByEtudiantAndQuizOrderByDateDesc(User etudiant, Quiz quiz);

    // ✅ Méthode ajoutée pour la suppression complète d’un quiz
    List<Resultat> findByQuiz(Quiz quiz);
}
