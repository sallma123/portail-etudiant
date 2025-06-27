package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Inscription;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.InscriptionRepository;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Utilisateur introuvable avec l'email : " + email)
        );
    }

    // 🔢 Nombre de cours auxquels l’étudiant est inscrit
    @Override
    public int countCoursInscrits(User etudiant) {
        return inscriptionRepository.findByEtudiant(etudiant).size();
    }

    // 🏆 Nombre de certificats obtenus (certificatObtenu = true)
    @Override
    public int countCertificatsObtenus(User etudiant) {
        return inscriptionRepository.countByEtudiantAndCertificatObtenuTrue(etudiant);
    }

    // 📈 Moyenne des progressions
    @Override
    public double getProgressionMoyenne(User etudiant) {
        List<Inscription> inscriptions = inscriptionRepository.findByEtudiant(etudiant);
        if (inscriptions.isEmpty()) return 0.0;

        double moyenne = inscriptions.stream()
                .mapToInt(Inscription::getProgression)
                .average()
                .orElse(0.0);

        return Math.round(moyenne * 10.0) / 10.0; // ex: 56.7 %
    }
}
