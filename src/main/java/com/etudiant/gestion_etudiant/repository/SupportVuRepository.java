package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.SupportVu;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportVuRepository extends JpaRepository<SupportVu, Long> {
    boolean existsByEtudiantAndSupport(User etudiant, Support support);
    long countByEtudiantAndSupport_Cours(User etudiant, Cours cours);
}
