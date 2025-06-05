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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    // 🔐 Login
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "resetSuccess", required = false) String resetSuccess,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", error.equals("unauthorized")
                    ? "Accès non autorisé." : "Identifiants invalides !");
        }
        if (resetSuccess != null) {
            model.addAttribute("message", "Votre mot de passe a été réinitialisé avec succès.");
        }
        return "login";
    }

    // 👤 Dashboards
    @GetMapping("/enseignant/dashboard")
    public String enseignantDashboard(Authentication auth) {
        return hasRole(auth, "ROLE_ENSEIGNANT") ? "enseignant" : "redirect:/login?error=unauthorized";
    }

    @GetMapping("/etudiant/dashboard")
    public String etudiantDashboard(Authentication auth) {
        return hasRole(auth, "ROLE_ETUDIANT") ? "etudiant" : "redirect:/login?error=unauthorized";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication auth, Model model) {
        if (hasRole(auth, "ROLE_ADMIN")) {
            model.addAttribute("nbEtudiants", userRepo.countByRole_Name("ROLE_ETUDIANT"));
            model.addAttribute("nbEnseignants", userRepo.countByRole_Name("ROLE_ENSEIGNANT"));
            return "admin-dashboard";
        }
        return "redirect:/login?error=unauthorized";
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    // 📚 Liste des cours
    @GetMapping("/enseignant/mes-cours")
    public String afficherMesCours(Model model,
                                   @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        model.addAttribute("coursList", coursService.getCoursParEnseignant(enseignant));
        return "mes-cours";
    }

    // ➕ Ajouter un cours
    @GetMapping("/enseignant/ajouter-cours")
    public String afficherFormulaireCours(Model model) {
        model.addAttribute("cours", new Cours());
        return "ajouter-cours";
    }

    @PostMapping("/enseignant/ajouter-cours")
    public String enregistrerCours(@ModelAttribute("cours") Cours cours,
                                   @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        coursService.ajouterCours(cours, enseignant);
        return "redirect:/enseignant/mes-cours";
    }

    // 📄 Liste des supports d’un cours
    @GetMapping("/enseignant/cours/{id}/supports")
    public String afficherListeSupports(@PathVariable Long id, Model model) {
        return coursService.getCoursParId(id).map(cours -> {
            model.addAttribute("cours", cours);
            model.addAttribute("supports", supportService.getSupportsParCours(cours));
            return "liste-supports";
        }).orElse("redirect:/enseignant/mes-cours");
    }

    // 📁 Ajouter un support
    @GetMapping("/enseignant/cours/{id}/ajouter-support")
    public String afficherFormulaireSupport(@PathVariable Long id, Model model) {
        return coursService.getCoursParId(id).map(cours -> {
            model.addAttribute("cours", cours);
            return "ajouter-support";
        }).orElse("redirect:/enseignant/mes-cours");
    }

    @PostMapping("/enseignant/cours/{id}/upload-support")
    public String uploadSupport(@PathVariable Long id,
                                @RequestParam("nomFichier") String nomFichier,
                                @RequestParam("type") String type,
                                @RequestParam("file") MultipartFile file) {
        try {
            Optional<Cours> coursOpt = coursService.getCoursParId(id);
            if (coursOpt.isEmpty()) return "redirect:/enseignant/mes-cours";

            String uploadPath = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);

            Support support = new Support();
            support.setNomFichier(nomFichier);
            support.setType(type);
            support.setLien("/fichiers/" + fileName);
            supportService.ajouterSupport(support, coursOpt.get());

            System.out.println("✅ Support uploadé : " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/enseignant/cours/" + id + "/supports";
    }

    // 🗑 Supprimer un support (et rester sur la page des supports)
    @PostMapping("/enseignant/support/{id}/supprimer")
    public String supprimerSupport(@PathVariable Long id) {
        Optional<Support> supportOpt = supportService.getSupportParId(id);
        if (supportOpt.isPresent()) {
            Long coursId = supportOpt.get().getCours().getId();

            // Supprimer le fichier
            String lien = supportOpt.get().getLien(); // ex: /fichiers/nom.pdf
            String nomFichier = lien.substring(lien.lastIndexOf("/") + 1);
            File fichier = new File(System.getProperty("user.dir") + "/uploads/" + nomFichier);
            if (fichier.exists()) fichier.delete();

            supportService.supprimerSupport(id);
            return "redirect:/enseignant/cours/" + coursId + "/supports";
        }
        return "redirect:/enseignant/mes-cours";
    }

    // 🗑 Supprimer un cours + ses supports
    @GetMapping("/enseignant/cours/{id}/supprimer")
    public String supprimerCours(@PathVariable Long id) {
        coursService.supprimerCoursEtSupports(id);
        return "redirect:/enseignant/mes-cours";
    }
}
