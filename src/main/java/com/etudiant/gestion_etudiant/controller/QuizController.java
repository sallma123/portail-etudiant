package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.dto.QuestionFormDto;
import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.Question;
import com.etudiant.gestion_etudiant.entity.Quiz;
import com.etudiant.gestion_etudiant.service.QuizService;
import com.etudiant.gestion_etudiant.repository.CoursRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enseignant")
public class QuizController {

    @Autowired private QuizService quizService;
    @Autowired private CoursRepository coursRepository;

    // ✅ Formulaire ajout quiz (utilise maintenant creer-quiz.html)
    @GetMapping("/cours/{id}/ajouter-quiz")
    public String formAjoutQuiz(@PathVariable Long id, Model model) {
        Cours cours = coursRepository.findById(id).orElseThrow();
        model.addAttribute("cours", cours);
        model.addAttribute("quiz", new Quiz());
        return "creer-quiz"; // 🔁 corriger ici (ancien: ajouter-quiz)
    }

    // ✅ Traitement ajout quiz
    @PostMapping("/cours/{id}/ajouter-quiz")
    public String ajouterQuiz(@PathVariable Long id, @RequestParam String titre, @RequestParam int seuil) {
        Cours cours = coursRepository.findById(id).orElseThrow();
        Quiz quiz = quizService.ajouterQuiz(titre, seuil, cours);
        return "redirect:/enseignant/quiz/" + quiz.getId() + "/creer-question";
    }

    // ✅ Formulaire stylé pour créer une question avec plusieurs bonnes réponses
    @GetMapping("/quiz/{quizId}/creer-question")
    public String creerQuestion(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("formDto", new QuestionFormDto());
        return "creer-quiz";
    }

    // ✅ Traitement du formulaire complet (question + réponses + corrects)
    @PostMapping("/quiz/{id}/valider-question")
    public String enregistrerQuestion(
            @PathVariable Long id,
            @ModelAttribute QuestionFormDto formDto
    ) {
        Quiz quiz = quizService.getQuizById(id);

        // Créer la question
        Question question = quizService.ajouterQuestion(formDto.getQuestion(), quiz);

        // Ajouter les réponses avec prise en charge de plusieurs bonnes
        List<String> choix = formDto.getChoix();
        List<Integer> corrects = formDto.getCorrects(); // index 1-based

        for (int i = 0; i < choix.size(); i++) {
            boolean correcte = corrects != null && corrects.contains(i + 1);
            quizService.ajouterReponse(choix.get(i), correcte, question);
        }

        return "redirect:/enseignant/quiz/" + quiz.getId() + "/creer-question";
    }
}
