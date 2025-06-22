package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.service.CoursService;
import com.etudiant.gestion_etudiant.service.SupportService;
import com.etudiant.gestion_etudiant.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enseignant/cours")
public class CoursController {

    @Autowired private CoursService coursService;
    @Autowired private SupportService supportService;
    @Autowired private UserService userService;

    // ✅ Liste des cours pour API
    @GetMapping
    public List<Cours> getCours(@AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        return coursService.getCoursParEnseignant(enseignant);
    }

    // ✅ Ajouter un cours via API (pas notification ici)
    @PostMapping
    public Cours ajouterCours(@RequestBody Cours cours,
                              @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        return coursService.ajouterCours(cours, enseignant);
    }

    // ✅ Modifier un cours via API
    @PutMapping("/{id}")
    public Cours modifierCours(@PathVariable Long id, @RequestBody Cours coursModifie) {
        return coursService.modifierCours(id, coursModifie);
    }

    // ✅ Supprimer un cours via API
    @DeleteMapping("/{id}")
    public void supprimerCours(@PathVariable Long id) {
        coursService.supprimerCours(id);
    }

    // ✅ Ajouter un support
    @PostMapping("/{id}/supports")
    public Support ajouterSupport(@PathVariable Long id, @RequestBody Support support) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isPresent()) {
            return supportService.ajouterSupport(support, coursOpt.get());
        } else {
            throw new RuntimeException("Cours non trouvé");
        }
    }

    // ✅ Lister les supports
    @GetMapping("/{id}/supports-api")
    public List<Support> getSupports(@PathVariable Long id) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isPresent()) {
            return supportService.getSupportsParCours(coursOpt.get());
        } else {
            throw new RuntimeException("Cours non trouvé");
        }
    }

}
