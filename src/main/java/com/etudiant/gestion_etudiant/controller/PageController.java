package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Support;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.CoursService;
import com.etudiant.gestion_etudiant.service.SupportService;
import com.etudiant.gestion_etudiant.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CoursService coursService;
    private final SupportService supportService;

    public PageController(UserRepository userRepo,
                          BCryptPasswordEncoder passwordEncoder,
                          UserService userService,
                          CoursService coursService,
                          SupportService supportService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.coursService = coursService;
        this.supportService = supportService;
    }

    // ✅ Page de login
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "resetSuccess", required = false) String resetSuccess,
                            Model model) {

        if (error != null) {
            model.addAttribute("error", error.equals("unauthorized")
                    ? "Accès non autorisé."
                    : "Identifiants invalides !");
        }

        if (resetSuccess != null) {
            model.addAttribute("message", "Votre mot de passe a été réinitialisé avec succès.");
        }

        return "login";
    }

    // ✅ Tableau de bord ADMIN
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication auth, Model model) {
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            long nbEtudiants = userRepo.countByRole_Name("ROLE_ETUDIANT");
            long nbEnseignants = userRepo.countByRole_Name("ROLE_ENSEIGNANT");

            model.addAttribute("nbEtudiants", nbEtudiants);
            model.addAttribute("nbEnseignants", nbEnseignants);

            return "admin-dashboard";
        }
        return "redirect:/login?error=unauthorized";
    }

    // ✅ Tableau de bord ÉTUDIANT
    @GetMapping("/etudiant/dashboard")
    public String etudiantDashboard(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"))) {
            return "etudiant";
        }
        return "redirect:/login?error=unauthorized";
    }

    // ✅ Tableau de bord ENSEIGNANT
    @GetMapping("/enseignant/dashboard")
    public String enseignantDashboard(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ENSEIGNANT"))) {
            return "enseignant";
        }
        return "redirect:/login?error=unauthorized";
    }

    // ✅ Liste des cours de l'enseignant connecté
    @GetMapping("/enseignant/mes-cours")
    public String afficherMesCours(Model model,
                                   @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        List<Cours> coursList = coursService.getCoursParEnseignant(enseignant);
        model.addAttribute("coursList", coursList);
        return "mes-cours";
    }

    // ✅ Formulaire de création d’un cours
    @GetMapping("/enseignant/ajouter-cours")
    public String afficherFormulaireCours(Model model) {
        model.addAttribute("cours", new Cours());
        return "ajouter-cours";
    }

    // ✅ Traitement d’enregistrement du cours
    @PostMapping("/enseignant/ajouter-cours")
    public String enregistrerCours(@ModelAttribute("cours") Cours cours,
                                   @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        coursService.ajouterCours(cours, enseignant);
        return "redirect:/enseignant/mes-cours";
    }

    // ✅ Formulaire pour ajouter un support à un cours
    @GetMapping("/enseignant/cours/{id}/ajouter-support")
    public String afficherFormulaireSupport(@PathVariable Long id, Model model) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isPresent()) {
            model.addAttribute("cours", coursOpt.get());
            model.addAttribute("support", new Support());
            return "ajouter-support";
        } else {
            return "redirect:/enseignant/mes-cours";
        }
    }

    // ✅ Traitement d’enregistrement du support
    @PostMapping("/enseignant/cours/{id}/ajouter-support")
    public String enregistrerSupport(@PathVariable Long id,
                                     @ModelAttribute("support") Support support) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        coursOpt.ifPresent(cours -> supportService.ajouterSupport(support, cours));
        return "redirect:/enseignant/mes-cours";
    }
    @GetMapping("/enseignant/cours/{id}/supports")
    public String afficherListeSupports(@PathVariable Long id, Model model) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isPresent()) {
            Cours cours = coursOpt.get();
            model.addAttribute("cours", cours);
            model.addAttribute("supports", supportService.getSupportsParCours(cours));
            return "liste-supports";
        }
        return "redirect:/enseignant/mes-cours";
    }

}
