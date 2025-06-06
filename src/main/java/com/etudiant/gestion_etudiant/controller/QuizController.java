package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.repository.*;
import com.etudiant.gestion_etudiant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class QuizController {

    @Autowired private QuizService quizService;
    @Autowired private CoursRepository coursRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ResultatService resultatService;
    @Autowired private CertificatGeneratorService certificatGeneratorService;
    @Autowired private ReponseRepository reponseRepository;

    // ✅ Partie Enseignant : Création quiz

    @GetMapping("/enseignant/cours/{id}/creer-quiz")
    public String afficherFormulaireQuiz(@PathVariable Long id, Model model,
                                         @RequestParam(value = "message", required = false) String message) {
        Cours cours = coursRepository.findById(id).orElseThrow();
        Quiz quiz = quizService.getQuizByCours(cours).orElse(null);

        model.addAttribute("cours", cours);
        model.addAttribute("quiz", quiz);
        model.addAttribute("message", message);

        return "creer-quiz";
    }

    @PostMapping("/enseignant/cours/{id}/enregistrer-quiz")
    public String enregistrerQuizVide(@PathVariable Long id,
                                      @RequestParam String titre,
                                      @RequestParam int seuil) {
        Cours cours = coursRepository.findById(id).orElseThrow();

        if (quizService.getQuizByCours(cours).isPresent()) {
            return "redirect:/enseignant/mes-cours";
        }

        Quiz quiz = quizService.ajouterQuiz(titre, seuil, cours);
        return "redirect:/enseignant/cours/" + id + "/creer-quiz";
    }

    @PostMapping("/enseignant/quiz/{id}/ajouter-question")
    public String ajouterQuestionAuQuiz(@PathVariable Long id,
                                        @RequestParam String titre,
                                        @RequestParam int seuil,
                                        @RequestParam String question,
                                        @RequestParam List<String> choix,
                                        @RequestParam(required = false) List<Integer> corrects,
                                        @RequestParam(required = false) String redirect,
                                        RedirectAttributes redirectAttributes) {

        if (corrects == null || corrects.isEmpty()) {
            redirectAttributes.addAttribute("message", "❌ Veuillez sélectionner au moins une bonne réponse.");
            return "redirect:/enseignant/cours/" + id + "/creer-quiz";
        }

        Quiz quiz = quizService.getQuizById(id);
        quiz.setTitre(titre);
        quiz.setSeuil(seuil);
        quizService.enregistrer(quiz);

        Question q = quizService.ajouterQuestion(question, quiz);
        for (int i = 0; i < choix.size(); i++) {
            boolean correcte = corrects.contains(i + 1);
            quizService.ajouterReponse(choix.get(i), correcte, q);
        }

        if ("mes-cours".equals(redirect)) {
            return "redirect:/enseignant/mes-cours";
        }

        redirectAttributes.addAttribute("message", "✅ Question ajoutée avec succès !");
        return "redirect:/enseignant/cours/" + quiz.getCours().getId() + "/creer-quiz";
    }

    @PostMapping("/enseignant/quiz/{id}/mettre-a-jour")
    public String mettreAJourQuiz(@PathVariable Long id,
                                  @RequestParam String titre,
                                  @RequestParam int seuil) {
        Quiz quiz = quizService.getQuizById(id);
        quiz.setTitre(titre);
        quiz.setSeuil(seuil);
        quizService.enregistrer(quiz);
        return "redirect:/enseignant/cours/" + quiz.getCours().getId() + "/creer-quiz";
    }

    @PostMapping("/enseignant/quiz/{id}/supprimer")
    public String supprimerQuiz(@PathVariable Long id) {
        quizService.supprimerQuiz(id);
        return "redirect:/enseignant/mes-cours";
    }

    // ✅ Partie Étudiant : Passer un quiz

    @GetMapping("/etudiant/quiz/{id}")
    public String afficherQuizPourEtudiant(@PathVariable Long id, Model model) {
        Quiz quiz = quizRepository.findById(id).orElseThrow();
        model.addAttribute("quiz", quiz);
        return "quiz-qcm";
    }

    @PostMapping("/etudiant/quiz/{id}/soumettre")
    public String soumettreQuiz(@PathVariable Long id,
                                @RequestParam Map<String, String> params,
                                Principal principal,
                                Model model) {
        User etudiant = userRepository.findByEmail(principal.getName()).orElseThrow();
        Map<Long, List<Long>> reponsesEtudiant = new HashMap<>();

        for (String key : params.keySet()) {
            if (key.startsWith("reponses[")) {
                Long qId = Long.parseLong(key.replace("reponses[", "").replace("]", ""));
                List<Long> rIds = Arrays.stream(params.get(key).split(","))
                        .map(Long::parseLong).collect(Collectors.toList());
                reponsesEtudiant.put(qId, rIds);
            }
        }

        double note = resultatService.corrigerEtEnregistrer(etudiant, id, reponsesEtudiant);
        Quiz quiz = quizRepository.findById(id).orElseThrow();

        String nomFichier = null;
        if (note >= quiz.getSeuil()) {
            nomFichier = etudiant.getPrenom().replaceAll(" ", "_") + "_" +
                    etudiant.getNom().replaceAll(" ", "_") + "_" +
                    quiz.getCours().getTitre().replaceAll(" ", "_") + ".pdf";

            certificatGeneratorService.generatePdf(
                    etudiant.getPrenom() + " " + etudiant.getNom(),
                    quiz.getCours().getTitre(),
                    note,
                    LocalDate.now()
            );
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("note", note);
        model.addAttribute("nomFichier", nomFichier); // pour lien /fichiers/...
        return "resultat-quiz";
    }

}
