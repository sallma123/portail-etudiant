package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.*;
import com.etudiant.gestion_etudiant.service.*;
import com.etudiant.gestion_etudiant.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // 🔹 Affiche les cours disponibles ET les cours inscrits (fusionnés dans une page)
    @GetMapping("/cours-disponibles")
    public String coursDisponibles(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        List<Cours> tousLesCours = coursRepository.findAll();
        List<Inscription> inscriptions = inscriptionRepository.findByEtudiant(etudiant);

        List<Cours> coursInscrits = inscriptions.stream()
                .map(Inscription::getCours)
                .collect(Collectors.toList());

        List<Cours> coursNonInscrits = tousLesCours.stream()
                .filter(c -> !coursInscrits.contains(c))
                .toList();

        model.addAttribute("coursDisponibles", coursNonInscrits);
        model.addAttribute("inscriptions", inscriptions); // Ajouté
        return "cours-disponibles"; // la page fusionnée
    }

    // 🔹 Action : s'inscrire à un cours
    @GetMapping("/s-inscrire/{coursId}")
    public String inscrire(@PathVariable Long coursId, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Cours cours = coursRepository.findById(coursId).orElseThrow();
        inscriptionService.inscrireEtudiant(etudiant, cours);
        return "redirect:/etudiant/cours-disponibles";
    }

    // ❌ SUPPRIMÉ : /mes-cours – car fusionné dans /cours-disponibles

    // 🔹 Affiche les supports d’un cours
    @GetMapping("/cours/{id}/supports")
    public String voirSupports(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Cours cours = coursRepository.findById(id).orElseThrow();

        if (!inscriptionRepository.existsByEtudiantAndCours(etudiant, cours)) {
            return "redirect:/etudiant/cours-disponibles";
        }

        List<Support> supports = supportRepository.findByCours(cours);
        Map<Long, Boolean> vus = new HashMap<>();

        for (Support s : supports) {
            boolean vu = supportVuService.estSupportVu(etudiant, s);
            vus.put(s.getId(), vu);
        }

        model.addAttribute("supports", supports);
        model.addAttribute("cours", cours);
        model.addAttribute("supportsVus", vus);
        return "support-etudiant";
    }


    // 🔹 Affiche un support ET le marque comme vu
    @GetMapping("/support/{id}/voir")
    public String voirSupport(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User etudiant = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Support support = supportRepository.findById(id).orElseThrow();
        supportVuService.marquerCommeVu(etudiant, support);

        Long coursId = support.getCours().getId();
        return "redirect:/etudiant/cours/" + coursId + "/supports";
    }
}
