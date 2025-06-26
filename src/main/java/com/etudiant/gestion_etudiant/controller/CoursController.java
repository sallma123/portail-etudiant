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

    // ✅ API : Liste des cours
    @GetMapping
    public List<Cours> getCours(@AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        return coursService.getCoursParEnseignant(enseignant);
    }

    // ✅ API : Ajouter un cours
    @PostMapping
    public Cours ajouterCours(@RequestBody Cours cours,
                              @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        return coursService.ajouterCours(cours, enseignant);
    }

    // ✅ API : Modifier un cours
    @PutMapping("/{id}")
    public Cours modifierCours(@PathVariable Long id, @RequestBody Cours coursModifie) {
        return coursService.modifierCours(id, coursModifie);
    }

    // ✅ API : Supprimer un cours
    @DeleteMapping("/{id}")
    public void supprimerCours(@PathVariable Long id) {
        coursService.supprimerCours(id);
    }

    // ✅ API : Ajouter un support
    @PostMapping("/{id}/supports")
    public Support ajouterSupport(@PathVariable Long id, @RequestBody Support support) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        return coursOpt.map(c -> supportService.ajouterSupport(support, c))
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
    }

    // ✅ API : Liste des supports
    @GetMapping("/{id}/supports-api")
    public List<Support> getSupports(@PathVariable Long id) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        return coursOpt.map(supportService::getSupportsParCours)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
    }
}
