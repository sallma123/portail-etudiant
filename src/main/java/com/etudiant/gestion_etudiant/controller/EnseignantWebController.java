package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.CoursService;
import com.etudiant.gestion_etudiant.service.NotificationService;
import com.etudiant.gestion_etudiant.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enseignant")
public class EnseignantWebController {

    @Autowired private UserService userService;
    @Autowired private CoursService coursService;
    @Autowired private NotificationService notificationService;
    @Autowired private UserRepository userRepository;

    // ✅ Formulaire ajout de cours
    @GetMapping("/ajouter-cours")
    public String afficherFormulaireAjout(Model model) {
        model.addAttribute("cours", new Cours());
        return "ajouter-cours";
    }

    // ✅ Enregistrement du cours avec notification
    @PostMapping("/ajouter-cours")
    public String enregistrerCours(@ModelAttribute("cours") Cours cours,
                                   @AuthenticationPrincipal(expression = "username") String email) {
        User enseignant = userService.findByEmail(email);
        Cours coursCree = coursService.ajouterCours(cours, enseignant);

        // 🔔 Notifier les étudiants
        List<User> etudiants = userRepository.findByRole_Name("ROLE_ETUDIANT");
        for (User etudiant : etudiants) {
            notificationService.envoyerNotification(etudiant,
                    "Nouveau cours disponible : " + coursCree.getTitre());
        }

        return "redirect:/enseignant/mes-cours";
    }
}
