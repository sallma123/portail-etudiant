package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.CoursRepository;
import com.etudiant.gestion_etudiant.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CoursService {

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private SupportRepository supportRepository;

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
}
