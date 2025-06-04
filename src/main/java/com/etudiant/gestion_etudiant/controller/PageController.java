package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public PageController(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
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
}
