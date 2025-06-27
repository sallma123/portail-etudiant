package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Notification;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender; // Pour envoyer des emails

    // 🔔 Créer et enregistrer une nouvelle notification non vue + envoyer un email
    public void envoyerNotification(User user, String message) {
        // Enregistrement base de données
        Notification notif = new Notification();
        notif.setDestinataire(user);
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());
        notif.setVue(false);
        notificationRepository.save(notif);

        // Envoi email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            try {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo(user.getEmail());
                email.setSubject("Nouvelle notification - AcadéLink");
                email.setText(message);
                mailSender.send(email);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email à " + user.getEmail());
                e.printStackTrace();
            }
        }
    }

    // 📥 Obtenir les 5 dernières notifications
    public List<Notification> getNotificationsPour(User user) {
        return notificationRepository.findTop5ByDestinataireOrderByDateDesc(user);
    }

    // 🔴 Obtenir les notifications non vues
    public List<Notification> getNotificationsNonVues(User user) {
        return notificationRepository.findByDestinataireAndVueFalseOrderByDateDesc(user);
    }

    // ✅ Marquer toutes les notifications comme vues
    public void marquerToutCommeVu(User user) {
        List<Notification> nonVues = getNotificationsNonVues(user);
        if (!nonVues.isEmpty()) {
            for (Notification notif : nonVues) {
                notif.setVue(true);
            }
            notificationRepository.saveAll(nonVues);
        }
    }

    // 🟢 Nombre de notifications non vues
    public int countNotificationsNonVues(User user) {
        return notificationRepository.countByDestinataireAndVueFalse(user);
    }
    public void supprimerNotificationsPourUtilisateur(User user) {
        notificationRepository.deleteByDestinataire(user);
    }

}
