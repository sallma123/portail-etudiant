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

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enseignant/cours")
public class CoursController {

    @Autowired
    private CoursService coursService;

    @Autowired
    private SupportService supportService;

    @Autowired
    private UserService userService;

    // ✅ Lister les cours de l'enseignant connecté
    @GetMapping
    public List<Cours> getCours(@AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        return coursService.getCoursParEnseignant(enseignant);
    }

    // ✅ Ajouter un nouveau cours
    @PostMapping
    public Cours ajouterCours(@RequestBody Cours cours, @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        return coursService.ajouterCours(cours, enseignant);
    }

    // ✅ Modifier un cours
    @PutMapping("/{id}")
    public Cours modifierCours(@PathVariable Long id, @RequestBody Cours coursModifie) {
        return coursService.modifierCours(id, coursModifie);
    }

    // ✅ Supprimer un cours
    @DeleteMapping("/{id}")
    public void supprimerCours(@PathVariable Long id) {
        coursService.supprimerCours(id);
    }

    // ✅ Ajouter un support à un cours
    @PostMapping("/{id}/supports")
    public Support ajouterSupport(@PathVariable Long id, @RequestBody Support support) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isPresent()) {
            return supportService.ajouterSupport(support, coursOpt.get());
        } else {
            throw new RuntimeException("Cours non trouvé");
        }
    }

    // ✅ Lister les supports d’un cours
    @GetMapping("/{id}/supports-api") // ou /supports/json
    public List<Support> getSupports(@PathVariable Long id) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isPresent()) {
            return supportService.getSupportsParCours(coursOpt.get());
        } else {
            throw new RuntimeException("Cours non trouvé");
        }
    }
}
