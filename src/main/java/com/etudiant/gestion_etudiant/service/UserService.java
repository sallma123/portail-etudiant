package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.User;

public interface UserService {
    User findByEmail(String email);

    // Dashboard étudiant
    int countCoursInscrits(User etudiant);
    int countCertificatsObtenus(User etudiant);
    double getProgressionMoyenne(User etudiant);
}
