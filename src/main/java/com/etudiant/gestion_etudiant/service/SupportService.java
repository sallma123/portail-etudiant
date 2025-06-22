package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.repository.SupportRepository;
import com.etudiant.gestion_etudiant.repository.SupportVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
@Service
public class SupportService {

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private SupportVuRepository supportVuRepository;

    // ✅ Ajouter un support
    public Support ajouterSupport(Support support, Cours cours) {
        support.setCours(cours);
        System.out.println("📥 Support enregistré pour le cours : " + cours.getTitre());
        return supportRepository.save(support);
    }

    // ✅ Supprimer un support en toute sécurité
    @Transactional
    public void supprimerSupport(Long id) {
        Optional<Support> supportOpt = supportRepository.findById(id);
        if (supportOpt.isPresent()) {
            Support support = supportOpt.get();

            // 1. Supprimer les traces de visionnage
            supportVuRepository.deleteBySupport(support);

            // 2. Supprimer le fichier physique s'il existe
            String cheminFichier = support.getLien().replace("/fichiers/", "uploads/");
            File fichier = new File(cheminFichier);
            if (fichier.exists()) {
                fichier.delete();
            }

            // 3. Supprimer le support
            supportRepository.delete(support);
        } else {
            throw new RuntimeException("Support introuvable");
        }
    }

    // ✅ Liste des supports pour un cours
    public List<Support> getSupportsParCours(Cours cours) {
        return supportRepository.findByCours(cours);
    }

    // ✅ Support par ID
    public Optional<Support> getSupportParId(Long id) {
        return supportRepository.findById(id);
    }
}
