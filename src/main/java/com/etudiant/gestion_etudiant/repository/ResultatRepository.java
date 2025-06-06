package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.entity.Resultat;
import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    Optional<Resultat> findByEtudiantAndQuiz(User etudiant, Quiz quiz);
}
