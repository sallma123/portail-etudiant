package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Utilisateur introuvable avec l'email : " + email)
        );
    }

    // 🔢 Nombre de cours inscrits (à remplacer avec vraie logique selon ta base)
    @Override
    public int countCoursInscrits(User etudiant) {
        return 0; // Remplace par le vrai count depuis InscriptionRepository
    }

    // 🏆 Nombre de certificats obtenus
    @Override
    public int countCertificatsObtenus(User etudiant) {
        return 0; // Remplace par le vrai count depuis Resultat/Certificat si tu l’as
    }

    // 📈 Progression moyenne sur les cours
    @Override
    public double getProgressionMoyenne(User etudiant) {
        return 0.0; // Remplace par la moyenne réelle des progressions
    }
}
