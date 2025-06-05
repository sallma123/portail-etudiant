package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Inscription;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InscriptionService {

    @Autowired
    private InscriptionRepository inscriptionRepository;

    public boolean inscrireEtudiant(User etudiant, Cours cours) {
        if (inscriptionRepository.existsByEtudiantAndCours(etudiant, cours)) {
            return false;
        }
        Inscription inscription = new Inscription();
        inscription.setEtudiant(etudiant);
        inscription.setCours(cours);
        inscription.setDateInscription(LocalDate.now());
        inscription.setProgression(0);
        inscription.setTermine(false);
        inscriptionRepository.save(inscription);
        return true;
    }

    public List<Inscription> getCoursInscrits(User etudiant) {
        return inscriptionRepository.findByEtudiant(etudiant);
    }

    public void mettreAJourProgression(User etudiant, Cours cours, int progression) {
        inscriptionRepository.findByEtudiantAndCours(etudiant, cours).ifPresent(inscription -> {
            inscription.setProgression(progression);
            if (progression == 100) inscription.setTermine(true);
            inscriptionRepository.save(inscription);
        });
    }
}
