package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long> {
    List<Support> findByCours(Cours cours); // Récupérer les supports d’un cours
}
