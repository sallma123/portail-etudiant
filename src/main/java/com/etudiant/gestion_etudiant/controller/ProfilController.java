package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ProfilController {

    @Autowired
    private UserService userService;

    // ✅ Affichage du formulaire de profil
    @GetMapping("/profil")
    public String afficherProfil(Model model, Principal principal) {
        String email = principal.getName(); // Récupère l'utilisateur connecté
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "profil"; // Va chercher templates/profil.html
    }

    // ✅ Traitement du formulaire de modification du profil
    @PostMapping("/profil")
    public String modifierProfil(@ModelAttribute("user") User userForm,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        User userExist = userService.findByEmail(email);

        // Mise à jour uniquement des champs autorisés
        userExist.setNom(userForm.getNom());
        userExist.setPrenom(userForm.getPrenom());
        userExist.setEmail(userForm.getEmail());

        userService.save(userExist); // Enregistrement dans la base

        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");
        return "redirect:/profil"; // Redirection après succès
    }
}
