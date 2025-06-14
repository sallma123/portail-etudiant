package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.MessagerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messagerie")
public class MessagerieController {

    @Autowired private UserRepository userRepository;
    @Autowired private MessagerieService messagerieService;

    // ✅ Page principale sans utilisateur sélectionné
    @GetMapping
    public String messagerie(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User me = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        List<User> autres = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .toList();

        model.addAttribute("utilisateurs", autres);
        model.addAttribute("utilisateurConnecte", me);
        model.addAttribute("autreUtilisateur", null);
        model.addAttribute("messages", List.of());

        return "messagerie";
    }

    // ✅ Afficher une discussion avec un utilisateur sélectionné
    @GetMapping("/discussion/{id}")
    public String afficherDiscussion(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        User me = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        User autre = userRepository.findById(id).orElseThrow();

        List<User> autresUtilisateurs = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .toList();

        List<MessagePrive> messages = messagerieService.getConversation(me, autre);

        model.addAttribute("utilisateurs", autresUtilisateurs);
        model.addAttribute("utilisateurConnecte", me);
        model.addAttribute("autreUtilisateur", autre);
        model.addAttribute("messages", messages);

        return "messagerie";
    }

    // ✅ Envoyer un message à l'utilisateur sélectionné
    @PostMapping("/envoyer/{id}")
    public String envoyerMessage(@PathVariable Long id,
                                 @RequestParam("contenu") String contenu,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User me = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        User destinataire = userRepository.findById(id).orElseThrow();

        messagerieService.envoyerMessage(me, destinataire, contenu);

        return "redirect:/messagerie/discussion/" + id;
    }
}
