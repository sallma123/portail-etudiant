package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.MessagePriveRepository;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagePriveRepository messagePriveRepository;

    @ModelAttribute("messagesRecents")
    public List<MessagePrive> chargerMessagesRecents(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return List.of();
        User me = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (me == null) return List.of();

        // Obtenir tous les messages où je suis soit expéditeur soit destinataire
        List<MessagePrive> tousMessages = messagePriveRepository
                .findByExpediteurOrDestinataireOrderByDateDesc(me, me);

        Map<Long, MessagePrive> conversationMap = new LinkedHashMap<>();

        for (MessagePrive msg : tousMessages) {
            User autre = msg.getExpediteur().equals(me) ? msg.getDestinataire() : msg.getExpediteur();
            if (!conversationMap.containsKey(autre.getId())) {
                conversationMap.put(autre.getId(), msg); // Ne garde que le dernier message échangé avec cette personne
            }
        }

        return new ArrayList<>(conversationMap.values());
    }
    @ModelAttribute("utilisateurConnecte")
    public User utilisateurConnecte(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

}
