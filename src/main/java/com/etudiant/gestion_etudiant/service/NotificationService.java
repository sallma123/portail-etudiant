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

    public void envoyerNotification(User user, String message) {
        Notification notif = new Notification();
        notif.setDestinataire(user);
        notif.setMessage(message);
        notif.setDate(LocalDateTime.now());
        notif.setVue(false);
        notificationRepository.save(notif);
    }

    public List<Notification> getNotificationsPour(User user) {
        return notificationRepository.findTop5ByDestinataireOrderByDateDesc(user);
    }

    public List<Notification> getNotificationsNonVues(User user) {
        return notificationRepository.findByDestinataireAndVueFalseOrderByDateDesc(user);
    }

    public void marquerToutCommeVu(User user) {
        List<Notification> nonVues = getNotificationsNonVues(user);
        for (Notification notif : nonVues) {
            notif.setVue(true);
        }
        notificationRepository.saveAll(nonVues);
    }
}
