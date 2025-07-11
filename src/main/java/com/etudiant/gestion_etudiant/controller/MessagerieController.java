package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.dto.UtilisateurAvecMessageDTO;
import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.MessagePriveRepository;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.MessagerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/messagerie")
public class MessagerieController {

    @Autowired private UserRepository userRepository;
    @Autowired private MessagerieService messagerieService;
    @Autowired private MessagePriveRepository messagePriveRepository;

    // ✅ Page principale sans utilisateur sélectionné
    @GetMapping
    public String messagerie(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User me = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        List<User> autres = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .filter(u -> {
                    String role = u.getRole().getName();
                    return role.equals("ROLE_ETUDIANT") || role.equals("ROLE_ENSEIGNANT");
                })
                .toList();

        List<UtilisateurAvecMessageDTO> utilisateurs = new ArrayList<>();
        for (User u : autres) {
            String dernierMsg = messagerieService.getDernierMessageEntre(me, u);
            utilisateurs.add(new UtilisateurAvecMessageDTO(u, dernierMsg));
        }

        model.addAttribute("utilisateurs", utilisateurs);
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

        // ❌ Bloquer les discussions avec des administrateurs
        String role = autre.getRole().getName();
        if (!role.equals("ROLE_ETUDIANT") && !role.equals("ROLE_ENSEIGNANT")) {
            return "redirect:/messagerie";
        }

        List<User> autres = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .filter(u -> {
                    String r = u.getRole().getName();
                    return r.equals("ROLE_ETUDIANT") || r.equals("ROLE_ENSEIGNANT");
                })
                .toList();

        List<UtilisateurAvecMessageDTO> utilisateurs = new ArrayList<>();
        for (User u : autres) {
            String dernierMsg = messagerieService.getDernierMessageEntre(me, u);
            utilisateurs.add(new UtilisateurAvecMessageDTO(u, dernierMsg));
        }

        List<MessagePrive> messages = messagerieService.getConversation(me, autre);

        model.addAttribute("utilisateurs", utilisateurs);
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

        // ❌ Ne pas permettre d’envoyer à un admin
        String role = destinataire.getRole().getName();
        if (!role.equals("ROLE_ETUDIANT") && !role.equals("ROLE_ENSEIGNANT")) {
            return "redirect:/messagerie";
        }

        messagerieService.envoyerMessage(me, destinataire, contenu);

        return "redirect:/messagerie/discussion/" + id;
    }

    // ✅ Marquer tous les messages reçus comme lus (pour le badge rouge)
    @PostMapping("/marquer-lus")
    @ResponseBody
    public void marquerMessagesCommeLus(@AuthenticationPrincipal UserDetails userDetails) {
        User me = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<MessagePrive> nonLus = messagePriveRepository.findByDestinataireAndLuFalse(me);
        for (MessagePrive msg : nonLus) {
            msg.setLu(true);
        }
        messagePriveRepository.saveAll(nonLus);
    }
}
