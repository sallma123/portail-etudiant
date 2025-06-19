package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.Notification;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.MessagePriveRepository;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.NotificationService;
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

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("messagesRecents")
    public List<MessagePrive> chargerMessagesRecents(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return List.of();
        User me = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (me == null) return List.of();

        List<MessagePrive> tousMessages = messagePriveRepository
                .findByExpediteurOrDestinataireOrderByDateDesc(me, me);

        Map<Long, MessagePrive> conversationMap = new LinkedHashMap<>();

        for (MessagePrive msg : tousMessages) {
            User autre = msg.getExpediteur().equals(me) ? msg.getDestinataire() : msg.getExpediteur();
            if (!conversationMap.containsKey(autre.getId())) {
                conversationMap.put(autre.getId(), msg);
            }
        }

        return new ArrayList<>(conversationMap.values());
    }

    @ModelAttribute("notifications")
    public List<Notification> chargerNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return List.of();
        User me = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (me == null) return List.of();

        return notificationService.getNotificationsPour(me);
    }

    @ModelAttribute("utilisateurConnecte")
    public User utilisateurConnecte(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
    @ModelAttribute("notifNonVues")
    public boolean afficherBadgeNotification(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return false;
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return false;
        return !notificationService.getNotificationsNonVues(user).isEmpty();
    }

}
