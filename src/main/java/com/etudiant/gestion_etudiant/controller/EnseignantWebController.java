package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.CoursService;
import com.etudiant.gestion_etudiant.service.NotificationService;
import com.etudiant.gestion_etudiant.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/enseignant")
public class EnseignantWebController {

    @Autowired private UserService userService;
    @Autowired private CoursService coursService;
    @Autowired private NotificationService notificationService;
    @Autowired private UserRepository userRepository;

    // ✅ Affichage de la page avec filtre
    @GetMapping("/mes-cours")
    public String afficherCoursParCategorie(@RequestParam(required = false) String categorie,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            Model model) {

        User enseignant = userService.findByEmail(userDetails.getUsername());

        List<Cours> coursList = (categorie == null || categorie.isEmpty())
                ? coursService.getCoursParEnseignant(enseignant)
                : coursService.getCoursParEnseignantEtCategorie(enseignant, categorie);

        model.addAttribute("coursList", coursList);
        model.addAttribute("categorie", categorie);
        return "mes-cours";
    }

    // ✅ Formulaire d’ajout
    @GetMapping("/ajouter-cours")
    public String afficherFormulaireAjout(Model model) {
        model.addAttribute("cours", new Cours());
        return "ajouter-cours";
    }

    // ✅ Enregistrement avec upload image et notification
    @PostMapping("/ajouter-cours")
    public String enregistrerCours(@ModelAttribute("cours") Cours cours,
                                   @RequestParam("imageFile") MultipartFile imageFile,
                                   @AuthenticationPrincipal(expression = "username") String email) {
        try {
            User enseignant = userService.findByEmail(email);

            // Upload image dans dossier uploads
            if (!imageFile.isEmpty()) {
                String uploadPath = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename().replaceAll("\\s+", "_");
                Path destinationPath = Paths.get(uploadPath, fileName);
                Files.copy(imageFile.getInputStream(), destinationPath);

                cours.setImage("/fichiers/" + fileName); // chemin accessible via /fichiers/**
            }

            Cours coursCree = coursService.ajouterCours(cours, enseignant);

            // 🔔 Notification à tous les étudiants
            List<User> etudiants = userRepository.findByRole_Name("ROLE_ETUDIANT");
            for (User etudiant : etudiants) {
                notificationService.envoyerNotification(etudiant,
                        "Nouveau cours disponible : " + coursCree.getTitre());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/enseignant/mes-cours";
    }

    // ✅ Suppression d’un cours (HTML)
    @GetMapping("/cours/{id}/supprimer")
    public String supprimerCours(@PathVariable Long id) {
        coursService.supprimerCours(id);
        return "redirect:/enseignant/mes-cours";
    }
}
