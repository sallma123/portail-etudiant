package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Notification;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // 🔔 Créer et enregistrer une nouvelle notification non vue
    public void envoyerNotification(User user, String message) {
        Notification notif = new Notification();
        notif.setDestinataire(user);
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());
        notif.setVue(false); // non vue au départ
        notificationRepository.save(notif);
    }

    // 📥 Obtenir les 5 dernières notifications, peu importe le statut (vue ou non)
    public List<Notification> getNotificationsPour(User user) {
        return notificationRepository.findTop5ByDestinataireOrderByDateDesc(user);
    }

    // 🔴 Obtenir les notifications non vues pour affichage du badge
    public List<Notification> getNotificationsNonVues(User user) {
        return notificationRepository.findByDestinataireAndVueFalseOrderByDateDesc(user);
    }

    // ✅ Marquer toutes les notifications comme vues (lues)
    public void marquerToutCommeVu(User user) {
        List<Notification> nonVues = getNotificationsNonVues(user);
        if (!nonVues.isEmpty()) {
            for (Notification notif : nonVues) {
                notif.setVue(true);
            }
            notificationRepository.saveAll(nonVues);
        }
    }

    // 🟢 Nombre de notifications non vues (pour le badge)
    public int countNotificationsNonVues(User user) {
        return notificationRepository.countByDestinataireAndVueFalse(user);
    }
}
