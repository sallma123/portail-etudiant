package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursRepository extends JpaRepository<Cours, Long> {
    List<Cours> findByEnseignant(User enseignant); // Récupérer les cours d’un enseignant
}
