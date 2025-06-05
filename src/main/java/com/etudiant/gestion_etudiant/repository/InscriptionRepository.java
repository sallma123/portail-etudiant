package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Inscription;
import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEtudiant(User etudiant);
    boolean existsByEtudiantAndCours(User etudiant, Cours cours);
    Optional<Inscription> findByEtudiantAndCours(User etudiant, Cours cours);
}
