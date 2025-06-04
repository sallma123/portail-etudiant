package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportService {

    @Autowired
    private SupportRepository supportRepository;

    // Ajouter un support à un cours
    public Support ajouterSupport(Support support, Cours cours) {
        support.setCours(cours);
        return supportRepository.save(support);
    }

    // Supprimer un support
    public void supprimerSupport(Long id) {
        supportRepository.deleteById(id);
    }

    // Récupérer tous les supports d’un cours
    public List<Support> getSupportsParCours(Cours cours) {
        return supportRepository.findByCours(cours);
    }
}
