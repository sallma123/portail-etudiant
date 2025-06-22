package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.User;
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


    // Ajouter un cours
    public Cours ajouterCours(Cours cours, User enseignant) {
        cours.setDateCreation(LocalDate.now());
        cours.setEnseignant(enseignant);
        return coursRepository.save(cours);
    }

    // Modifier un cours
    public Cours modifierCours(Long id, Cours coursModifie) {
        Optional<Cours> coursOpt = coursRepository.findById(id);
        if (coursOpt.isPresent()) {
            Cours cours = coursOpt.get();
            cours.setTitre(coursModifie.getTitre());
            cours.setDescription(coursModifie.getDescription());
            cours.setCategorie(coursModifie.getCategorie());
            return coursRepository.save(cours);
        } else {
            throw new RuntimeException("Cours introuvable");
        }
    }

    // Supprimer un cours et ses supports
    @Transactional
    public void supprimerCoursEtSupports(Long id) {
        Optional<Cours> coursOpt = coursRepository.findById(id);
        if (coursOpt.isPresent()) {
            Cours cours = coursOpt.get();
            List<Support> supports = supportRepository.findByCours(cours);

            // Supprimer les fichiers physiques associés (PDF, vidéos, etc.)
            for (Support support : supports) {
                String cheminFichier = support.getLien().replace("/fichiers/", "uploads/");
                File fichier = new File(cheminFichier);
                if (fichier.exists()) {
                    fichier.delete();
                }
            }

            // Supprimer les supports de la base
            supportRepository.deleteAll(supports);

            // Supprimer le cours
            coursRepository.delete(cours);
        } else {
            throw new RuntimeException("Cours introuvable");
        }
    }

    // Récupérer tous les cours d’un enseignant
    public List<Cours> getCoursParEnseignant(User enseignant) {
        return coursRepository.findByEnseignant(enseignant);
    }

    // Récupérer un cours par ID
    public Optional<Cours> getCoursParId(Long id) {
        return coursRepository.findById(id);
    }

    // 🔢 Nombre total de cours (admin)
    public long count() {
        return coursRepository.count();
    }

    // 📊 Statistiques globales (admin)
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

    // 🔢 Nombre d'étudiants par enseignant (factice)
    public int countEtudiantsParEnseignant(User enseignant) {
        return 0; // à remplacer par une vraie logique avec inscriptions
    }

    // 🔢 Nombre de certificats délivrés (factice)
    public int countCertificatsParEnseignant(User enseignant) {
        return 0; // à remplacer par une vraie logique avec certificats
    }

    // 📈 Statistiques par cours (enseignant)
    public List<Map<String, Object>> getStatistiquesParCours(User enseignant) {
        List<Cours> coursList = coursRepository.findByEnseignant(enseignant);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Cours cours : coursList) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("nomCours", cours.getTitre());
            stat.put("nbEtudiants", 0); // À remplacer par vraie logique
            stat.put("moyenne", "14.2 / 20"); // Exemple statique
            stat.put("tauxReussite", 68); // Pour test
            stat.put("nbCommentaires", 12); // Exemple statique
            stats.add(stat);
        }
        return stats;
    }

    // 📋 Cours de l'étudiant avec statistiques
    public List<Map<String, Object>> getCoursEtudiantAvecStats(User etudiant) {
        List<Map<String, Object>> coursEtudiant = new ArrayList<>();
        // À implémenter avec les vraies données d’inscriptions
        return coursEtudiant;
    }
}
