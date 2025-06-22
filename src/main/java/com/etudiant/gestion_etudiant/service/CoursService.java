package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.entity.Inscription;
import com.etudiant.gestion_etudiant.repository.CoursRepository;
import com.etudiant.gestion_etudiant.repository.InscriptionRepository;
import com.etudiant.gestion_etudiant.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoursService {

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private ForumService forumService;

    // Ajouter un cours
    public Cours ajouterCours(Cours cours, User enseignant) {
        cours.setDateCreation(LocalDate.now());
        cours.setEnseignant(enseignant);
        return coursRepository.save(cours);
    }

    // Modifier un cours
    public Cours modifierCours(Long id, Cours coursModifie) {
        return coursRepository.findById(id).map(cours -> {
            cours.setTitre(coursModifie.getTitre());
            cours.setDescription(coursModifie.getDescription());
            cours.setCategorie(coursModifie.getCategorie());
            return coursRepository.save(cours);
        }).orElseThrow(() -> new RuntimeException("Cours introuvable"));
    }

    // Supprimer un cours et ses supports
    @Transactional
    public void supprimerCoursEtSupports(Long id) {
        Optional<Cours> coursOpt = coursRepository.findById(id);
        if (coursOpt.isPresent()) {
            Cours cours = coursOpt.get();
            List<Support> supports = supportRepository.findByCours(cours);

            // Supprimer les fichiers physiques
            for (Support support : supports) {
                String cheminFichier = support.getLien().replace("/fichiers/", "uploads/");
                File fichier = new File(cheminFichier);
                if (fichier.exists()) {
                    fichier.delete();
                }
            }

            supportRepository.deleteAll(supports);
            coursRepository.delete(cours);
        } else {
            throw new RuntimeException("Cours introuvable");
        }
    }

    // Récupérer les cours d’un enseignant
    public List<Cours> getCoursParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant);
    }

    // Récupérer un cours par ID
    public Optional<Cours> getCoursParId(Long id) {
        return coursRepository.findById(id);
    }

    // Nombre total de cours (admin)
    public long count() {
        return coursRepository.count();
    }

    // Statistiques globales pour admin
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

    // Nombre d'étudiants par enseignant
    public int countEtudiantsParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant).stream()
                .mapToInt(cours -> inscriptionRepository.countByCours(cours)).sum();
    }

    // Nombre de certificats par enseignant
    public int countCertificatsParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant).stream()
                .mapToInt(cours -> inscriptionRepository.countByCoursAndCertificatObtenuTrue(cours)).sum();
    }

    // Statistiques par cours pour enseignant
    public List<Map<String, Object>> getStatistiquesParCours(User enseignant) {
        List<Cours> coursList = coursRepository.findByEnseignant(enseignant);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Cours cours : coursList) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("nomCours", cours.getTitre());
            stat.put("nbEtudiants", inscriptionRepository.countByCours(cours));
            stat.put("moyenne", "14.2 / 20"); // peut être calculée si tu as les notes
            stat.put("tauxReussite", 68); // à améliorer si besoin
            stat.put("nbCommentaires", forumService.getMessagesParCours(cours).size());
            stats.add(stat);
        }
        return stats;
    }

    // Cours de l'étudiant avec stats
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
}
