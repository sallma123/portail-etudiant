package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Inscription;
import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.service.CoursService;
import com.etudiant.gestion_etudiant.service.ForumService;
import com.etudiant.gestion_etudiant.repository.InscriptionRepository;
import com.etudiant.gestion_etudiant.repository.SupportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminCoursController {

    @Autowired
    private CoursService coursService;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private ForumService forumService;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @GetMapping("/cours/{id}")
    public String detailCoursAdmin(@PathVariable Long id, Model model) {
        Optional<Cours> coursOpt = coursService.getCoursParId(id);
        if (coursOpt.isEmpty()) {
            return "redirect:/admin/cours?error=cours-introuvable";
        }

        Cours cours = coursOpt.get();

        List<User> etudiants = inscriptionRepository.findByCours(cours)
                .stream()
                .map(Inscription::getEtudiant)
                .collect(Collectors.toList());

        model.addAttribute("cours", cours);
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("supports", supportRepository.findByCours(cours));
        Quiz quiz = cours.getQuiz();
        if (quiz != null && quiz.getQuestions() != null) {
            quiz.getQuestions().size(); // force le chargement
        }
        model.addAttribute("quiz", quiz);


        // ✅ Ajoute cette ligne pour éviter le null
        model.addAttribute("questions", cours.getQuiz() != null && cours.getQuiz().getQuestions() != null
                ? cours.getQuiz().getQuestions()
                : List.of());

        model.addAttribute("messagesForum", forumService.getMessagesParCours(cours));

        return "detail-cours";
    }
    @GetMapping("/cours/supprimer/{id}")
    public String supprimerCours(@PathVariable Long id) {
        coursService.supprimerCours(id);  // Ce service doit supprimer le cours et ses dépendances
        return "redirect:/admin/dashboard"; // ou /admin/cours selon ta route
    }

}
