package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.Notification;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    // 🔔 Liste des 5 dernières notifications
    @GetMapping("/recents")
    public List<Notification> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return notificationService.getNotificationsPour(user);
    }

    // 🔴 Nombre de notifications non vues (pour badge rouge)
    @GetMapping("/non-vues")
    public int getNombreNonVues(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return notificationService.countNotificationsNonVues(user);
    }

    // ✅ Marquer toutes les notifications comme vues
    @PostMapping("/marquer-vues")
    public void marquerCommeVues(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        notificationService.marquerToutCommeVu(user);
    }
}
