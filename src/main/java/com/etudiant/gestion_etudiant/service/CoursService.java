package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.dto.StatistiqueCours;
import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoursService {

    @Autowired private CoursRepository coursRepository;
    @Autowired private SupportRepository supportRepository;
    @Autowired private InscriptionRepository inscriptionRepository;
    @Autowired private ForumService forumService;
    @Autowired private InscriptionService inscriptionService;
    @Autowired private QuizRepository quizRepository;

    // ✅ Ajouter un cours
    public Cours ajouterCours(Cours cours, User enseignant) {
        cours.setDateCreation(LocalDate.now());
        cours.setEnseignant(enseignant);
        return coursRepository.save(cours);
    }

    // ✅ Modifier un cours
    public Cours modifierCours(Long id, Cours coursModifie) {
        return coursRepository.findById(id).map(cours -> {
            cours.setTitre(coursModifie.getTitre());
            cours.setDescription(coursModifie.getDescription());
            cours.setCategorie(coursModifie.getCategorie());
            return coursRepository.save(cours);
        }).orElseThrow(() -> new RuntimeException("Cours introuvable"));
    }

    // ✅ Supprimer un cours avec tous ses éléments liés
    @Transactional
    public void supprimerCours(Long id) {
        Optional<Cours> coursOpt = coursRepository.findById(id);
        if (coursOpt.isPresent()) {
            Cours cours = coursOpt.get();

            // 1. Supprimer les fichiers physiques des supports
            List<Support> supports = supportRepository.findByCours(cours);
            for (Support support : supports) {
                String cheminFichier = support.getLien().replace("/fichiers/", "uploads/");
                File fichier = new File(cheminFichier);
                if (fichier.exists()) {
                    fichier.delete();
                }
            }

            // 2. Supprimer les inscriptions
            List<Inscription> inscriptions = inscriptionRepository.findByCours(cours);
            inscriptionRepository.deleteAll(inscriptions);

            // 3. Supprimer les messages de forum liés
            forumService.supprimerMessagesParCours(cours);

            // 4. Supprimer le quiz associé s’il existe
            if (cours.getQuiz() != null) {
                quizRepository.delete(cours.getQuiz());
            }

            // 5. Supprimer les supports (en base)
            supportRepository.deleteAll(supports);

            // 6. Supprimer le cours lui-même
            coursRepository.delete(cours);
        } else {
            throw new RuntimeException("Cours introuvable");
        }
    }

    public List<Cours> getCoursParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant);
    }

    public Optional<Cours> getCoursParId(Long id) {
        return coursRepository.findById(id);
    }

    public long count() {
        return coursRepository.count();
    }

    public List<Map<String, Object>> findAllWithStats() {
        List<Cours> coursList = coursRepository.findAll();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Cours cours : coursList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cours.getId());
            map.put("titre", cours.getTitre());
            map.put("enseignant", cours.getEnseignant());
            map.put("nbEtudiants", inscriptionRepository.countByCours(cours));
            map.put("quiz", cours.getQuiz());
            stats.add(map);
        }
        return stats;
    }

    public int countEtudiantsParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant).stream()
                .mapToInt(c -> inscriptionRepository.countByCours(c)).sum();
    }

    public int countCertificatsParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant).stream()
                .mapToInt(c -> inscriptionRepository.countByCoursAndCertificatObtenuTrue(c)).sum();
    }

    public List<StatistiqueCours> getStatistiquesParCours(User enseignant) {
        List<Cours> coursList = coursRepository.findByEnseignant(enseignant);
        List<StatistiqueCours> stats = new ArrayList<>();

        for (Cours cours : coursList) {
            List<Inscription> inscriptions = inscriptionRepository.findByCours(cours);
            int nbEtudiants = inscriptions.size();
            int nbCertificats = (int) inscriptions.stream().filter(Inscription::isCertificatObtenu).count();
            int nbCommentaires = forumService.getMessagesParCours(cours).size();

            double moyenne = inscriptions.stream()
                    .filter(i -> i.getNote() != null)
                    .mapToDouble(Inscription::getNote)
                    .average()
                    .orElse(0.0);
            String moyenneStr = String.format("%.1f %%", moyenne);

            int tauxReussite = nbEtudiants == 0 ? 0 : (nbCertificats * 100 / nbEtudiants);

            stats.add(new StatistiqueCours(
                    cours.getTitre(),
                    nbEtudiants,
                    nbCertificats,
                    moyenneStr,
                    tauxReussite,
                    nbCommentaires
            ));
        }

        return stats;
    }

    public List<Map<String, Object>> getCoursEtudiantAvecStats(User etudiant) {
        return inscriptionRepository.findByEtudiant(etudiant).stream().map(inscription -> {
            Map<String, Object> map = new HashMap<>();
            Cours cours = inscription.getCours();
            map.put("nom", cours.getTitre());
            map.put("note", inscription.getNote() != null ? inscription.getNote() : "N/A");
            map.put("certificat", inscription.isCertificatObtenu());
            map.put("progression", inscription.getProgression());
            map.put("nbCommentaires", forumService.getMessagesParCours(cours).size());
            return map;
        }).collect(Collectors.toList());
    }
    public List<Cours> getCoursParEnseignantEtCategorie(User enseignant, String categorie) {
        return coursRepository.findByEnseignantAndCategorie(enseignant, categorie);
    }

}
