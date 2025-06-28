package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.repository.*;
import com.etudiant.gestion_etudiant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/etudiant")
public class EtudiantController {

    @Autowired private CoursRepository coursRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private InscriptionService inscriptionService;
    @Autowired private SupportRepository supportRepository;
    @Autowired private SupportVuService supportVuService;
    @Autowired private InscriptionRepository inscriptionRepository;
    @Autowired private QuizService quizService;
    @Autowired private ResultatService resultatService;
    @Autowired private ForumService forumService;
    @Autowired private NotificationService notificationService;

    @GetMapping("/cours-disponibles")
    public String coursDisponibles(@RequestParam(required = false) String categorie,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        List<Cours> tousLesCours = coursRepository.findAll();
        List<Inscription> inscriptions = inscriptionRepository.findByEtudiant(etudiant);

        List<Cours> coursInscrits = inscriptions.stream()
                .map(Inscription::getCours)
                .collect(Collectors.toList());

        List<Cours> coursNonInscrits = tousLesCours.stream()
                .filter(c -> !coursInscrits.contains(c))
                .collect(Collectors.toList());

        if (categorie != null && !categorie.isEmpty()) {
            coursNonInscrits = coursNonInscrits.stream()
                    .filter(c -> c.getCategorie() != null && c.getCategorie().equalsIgnoreCase(categorie))
                    .collect(Collectors.toList());
        }

        model.addAttribute("coursDisponibles", coursNonInscrits);
        model.addAttribute("inscriptions", inscriptions);
        model.addAttribute("categorie", categorie);
        return "cours-disponibles";
    }

    @GetMapping("/s-inscrire/{coursId}")
    public String inscrire(@PathVariable Long coursId, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Cours cours = coursRepository.findById(coursId).orElseThrow();

        inscriptionService.inscrireEtudiant(etudiant, cours);

        String message = etudiant.getPrenom() + " " + etudiant.getNom() + " s’est inscrit à votre cours : " + cours.getTitre();
        notificationService.envoyerNotification(cours.getEnseignant(), message);

        return "redirect:/etudiant/cours-disponibles";
    }

    @GetMapping("/cours/{id}/supports")
    public String voirSupports(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Cours cours = coursRepository.findById(id).orElseThrow();

        if (!inscriptionRepository.existsByEtudiantAndCours(etudiant, cours)) {
            return "redirect:/etudiant/cours-disponibles";
        }

        List<Support> supports = supportRepository.findByCours(cours);
        Map<Long, Boolean> vus = new HashMap<>();
        boolean supportsNonVus = false;

        for (Support s : supports) {
            boolean vu = supportVuService.estSupportVu(etudiant, s);
            vus.put(s.getId(), vu);
            if (!vu) supportsNonVus = true;
        }

        model.addAttribute("supports", supports);
        model.addAttribute("cours", cours);
        model.addAttribute("supportsVus", vus);
        model.addAttribute("supportsNonVus", supportsNonVus);

        Optional<Quiz> quizOpt = quizService.getQuizByCours(cours);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            model.addAttribute("quizExiste", true);
            model.addAttribute("quiz", quiz);

            // ✅ Vérifie si tous les supports ont été vus
            boolean peutPasserQuiz = supportVuService.tousLesSupportsVus(etudiant, cours);
            model.addAttribute("peutPasserQuiz", peutPasserQuiz);

            Optional<Inscription> inscriptionOpt = inscriptionRepository.findByEtudiantAndCours(etudiant, cours);
            boolean certificatDisponible = false;

            if (inscriptionOpt.isPresent()) {
                Inscription inscription = inscriptionOpt.get();
                certificatDisponible = inscription.isCertificatObtenu(); // ✅ vérifie le bon champ

                if (certificatDisponible) {
                    String nomFichier = etudiant.getPrenom().replaceAll(" ", "_") + "_" +
                            etudiant.getNom().replaceAll(" ", "_") + "_" +
                            cours.getTitre().replaceAll(" ", "_") + ".pdf";
                    model.addAttribute("nomFichier", nomFichier);
                }
            }
            model.addAttribute("certificatDisponible", certificatDisponible);

        } else {
            model.addAttribute("quizExiste", false);
        }

        model.addAttribute("messages", forumService.getMessagesParCours(cours));
        model.addAttribute("nouveauMessage", new MessageForum());
        model.addAttribute("utilisateurConnecte", etudiant);

        return "support-etudiant";
    }

    @PostMapping("/cours/{id}/forum-ajouter")
    public String posterMessageForum(@PathVariable Long id,
                                     @RequestParam("contenu") String contenu,
                                     @AuthenticationPrincipal UserDetails userDetails) {

        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Cours cours = coursRepository.findById(id).orElseThrow();

        MessageForum message = new MessageForum();
        message.setAuteur(etudiant);
        message.setCours(cours);
        message.setContenu(contenu);
        message.setDate(LocalDateTime.now());
        forumService.ajouterMessage(message);

        List<Inscription> inscriptions = inscriptionRepository.findByCours(cours);
        for (Inscription insc : inscriptions) {
            User autreEtudiant = insc.getEtudiant();
            if (!autreEtudiant.getId().equals(etudiant.getId())) {
                notificationService.envoyerNotification(
                        autreEtudiant,
                        "Nouveau commentaire dans le forum du cours : " + cours.getTitre()
                );
            }
        }

        User enseignant = cours.getEnseignant();
        if (!enseignant.getId().equals(etudiant.getId())) {
            notificationService.envoyerNotification(
                    enseignant,
                    etudiant.getPrenom() + " a commenté dans le forum du cours : " + cours.getTitre()
            );
        }

        return "redirect:/etudiant/cours/" + id + "/supports";
    }

    @GetMapping("/support/{id}/voir")
    public String voirSupport(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Support support = supportRepository.findById(id).orElseThrow();
        supportVuService.marquerCommeVu(etudiant, support);

        Long coursId = support.getCours().getId();
        return "redirect:/etudiant/cours/" + coursId + "/supports";
    }

    @PostMapping("/cours/{coursId}/forum/{messageId}/supprimer")
    public String supprimerMessageForum(@PathVariable Long coursId,
                                        @PathVariable Long messageId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        forumService.supprimerMessage(messageId, user);
        return "redirect:/etudiant/cours/" + coursId + "/supports";
    }
}
