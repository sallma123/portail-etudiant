package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Question;
import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.repository.CoursRepository;
import com.etudiant.gestion_etudiant.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/enseignant")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CoursRepository coursRepository;

    // ✅ Affiche le formulaire de création ou d'ajout de questions
    @GetMapping("/cours/{id}/creer-quiz")
    public String afficherFormulaireQuiz(@PathVariable Long id, Model model,
                                         @RequestParam(value = "message", required = false) String message) {
        Cours cours = coursRepository.findById(id).orElseThrow();
        Quiz quiz = quizService.getQuizByCours(cours).orElse(null);

        model.addAttribute("cours", cours);
        model.addAttribute("quiz", quiz);
        model.addAttribute("message", message);

        return "creer-quiz";
    }

    // ✅ Crée un quiz vide avec titre et seuil
    @PostMapping("/cours/{id}/enregistrer-quiz")
    public String enregistrerQuizVide(
            @PathVariable Long id,
            @RequestParam String titre,
            @RequestParam int seuil
    ) {
        Cours cours = coursRepository.findById(id).orElseThrow();

        if (quizService.getQuizByCours(cours).isPresent()) {
            return "redirect:/enseignant/mes-cours";
        }

        Quiz quiz = quizService.ajouterQuiz(titre, seuil, cours);
        return "redirect:/enseignant/cours/" + id + "/creer-quiz";
    }

    // ✅ Ajoute une question à un quiz existant
    @PostMapping("/quiz/{id}/ajouter-question")
    public String ajouterQuestionAuQuiz(
            @PathVariable Long id,
            @RequestParam String titre,
            @RequestParam int seuil,
            @RequestParam String question,
            @RequestParam List<String> choix,
            @RequestParam(required = false) List<Integer> corrects,
            @RequestParam(required = false) String redirect,
            RedirectAttributes redirectAttributes
    ) {
        Quiz quiz = quizService.getQuizById(id);
        quiz.setTitre(titre);
        quiz.setSeuil(seuil);
        quizService.enregistrer(quiz);

        Question q = quizService.ajouterQuestion(question, quiz);
        for (int i = 0; i < choix.size(); i++) {
            boolean correcte = corrects != null && corrects.contains(i + 1);
            quizService.ajouterReponse(choix.get(i), correcte, q);
        }

        if ("mes-cours".equals(redirect)) {
            return "redirect:/enseignant/mes-cours";
        }

        redirectAttributes.addAttribute("message", "✅ Question ajoutée avec succès !");
        return "redirect:/enseignant/cours/" + quiz.getCours().getId() + "/creer-quiz";
    }


    // ✅ Met à jour titre et seuil d’un quiz
    @PostMapping("/quiz/{id}/mettre-a-jour")
    public String mettreAJourQuiz(@PathVariable Long id,
                                  @RequestParam String titre,
                                  @RequestParam int seuil) {
        Quiz quiz = quizService.getQuizById(id);
        quiz.setTitre(titre);
        quiz.setSeuil(seuil);
        quizService.enregistrer(quiz);
        return "redirect:/enseignant/cours/" + quiz.getCours().getId() + "/creer-quiz";
    }

    // ✅ Supprime complètement un quiz
    @PostMapping("/quiz/{id}/supprimer")
    public String supprimerQuiz(@PathVariable Long id) {
        quizService.supprimerQuiz(id);
        return "redirect:/enseignant/mes-cours";
    }
}
