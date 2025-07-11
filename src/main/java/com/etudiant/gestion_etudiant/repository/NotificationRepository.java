package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Notification;
import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop5ByDestinataireOrderByDateDesc(User destinataire);

    List<Notification> findByDestinataireAndVueFalseOrderByDateDesc(User destinataire);

    // 🔴 Pour compter les notifications non vues (badge rouge)
    int countByDestinataireAndVueFalse(User destinataire);
    void deleteByDestinataire(User destinataire);

}
