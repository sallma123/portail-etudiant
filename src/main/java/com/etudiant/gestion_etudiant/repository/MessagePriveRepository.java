// com.etudiant.gestion_etudiant.repository.MessagePriveRepository.java
package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessagePriveRepository extends JpaRepository<MessagePrive, Long> {

    List<MessagePrive> findTop5ByDestinataireOrderByDateDesc(User user);

    @Query("SELECT m FROM MessagePrive m WHERE " +
            "(m.expediteur = :u1 AND m.destinataire = :u2) OR " +
            "(m.expediteur = :u2 AND m.destinataire = :u1) " +
            "ORDER BY m.date ASC")
    List<MessagePrive> findByExpediteurAndDestinataireOrViceVersa(User u1, User u2);
    List<MessagePrive> findByExpediteurOrDestinataireOrderByDateDesc(User expediteur, User destinataire);

}