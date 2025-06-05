package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.repository.SupportRepository;
import com.etudiant.gestion_etudiant.repository.SupportVuRepository;
import com.etudiant.gestion_etudiant.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SupportVuService {

    @Autowired
    private SupportVuRepository supportVuRepository;

    @Autowired
    private InscriptionService inscriptionService;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    public boolean estSupportVu(User etudiant, Support support) {
        return supportVuRepository.existsByEtudiantAndSupport(etudiant, support);
    }

    public void marquerCommeVu(User etudiant, Support support) {
        if (!supportVuRepository.existsByEtudiantAndSupport(etudiant, support)) {
            SupportVu supportVu = new SupportVu();
            supportVu.setEtudiant(etudiant);
            supportVu.setSupport(support);
            supportVu.setDateVue(LocalDate.now());
            supportVuRepository.save(supportVu);

            // Recalculer la progression
            Cours cours = support.getCours();
            long totalSupports = supportRepository.countByCours(cours);
            long supportsVus = supportVuRepository.countByEtudiantAndSupport_Cours(etudiant, cours);

            int progression = (int) ((supportsVus / (double) totalSupports) * 100);
            inscriptionService.mettreAJourProgression(etudiant, cours, progression);
        }
    }
}
