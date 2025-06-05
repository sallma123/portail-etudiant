package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Question;
import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.service.QuizService;
import com.etudiant.gestion_etudiant.repository.CoursRepository;

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

    // ✅ Créer automatiquement un quiz s'il n'existe pas
    @GetMapping("/cours/{id}/ajouter-quiz")
    public String afficherOuCreerQuiz(@PathVariable Long id, Model model) {
        Cours cours = coursRepository.findById(id).orElseThrow();

        Quiz quiz = quizService.getQuizByCours(cours).orElse(null);
        if (quiz == null) {
            quiz = quizService.ajouterQuiz("Quiz du cours : " + cours.getTitre(), 50, cours);
        }

        model.addAttribute("quiz", quiz);
        return "creer-quiz";
    }

    // ✅ Affiche le formulaire d'ajout de questions
    @GetMapping("/quiz/{quizId}/creer-question")
    public String creerQuestion(@PathVariable Long quizId, Model model,
                                @ModelAttribute("message") String message) {
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("message", message);
        return "creer-quiz";
    }

    // ✅ Enregistre une nouvelle question avec ses réponses
    @PostMapping("/quiz/{id}/valider-question")
    public String enregistrerQuestion(
            @PathVariable Long id,
            @RequestParam String question,
            @RequestParam List<String> choix,
            @RequestParam(required = false) List<Integer> corrects,
            RedirectAttributes redirectAttributes
    ) {
        Quiz quiz = quizService.getQuizById(id);
        Question q = quizService.ajouterQuestion(question, quiz);

        for (int i = 0; i < choix.size(); i++) {
            boolean correcte = corrects != null && corrects.contains(i + 1);
            quizService.ajouterReponse(choix.get(i), correcte, q);
        }

        redirectAttributes.addFlashAttribute("message", "✅ Question enregistrée avec succès !");
        return "redirect:/enseignant/quiz/" + id + "/creer-question";
    }

    // ✅ Supprime un quiz existant
    @PostMapping("/quiz/{id}/supprimer")
    public String supprimerQuiz(@PathVariable Long id) {
        quizService.supprimerQuiz(id);
        return "redirect:/enseignant/mes-cours";
    }
}
