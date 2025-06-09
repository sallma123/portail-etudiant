// com.etudiant.gestion_etudiant.service.MessagerieService.java
package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.MessagePriveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessagerieService {

    @Autowired
    private MessagePriveRepository messagePriveRepository;

    public void envoyerMessage(User expediteur, User destinataire, String contenu) {
        MessagePrive msg = new MessagePrive();
        msg.setExpediteur(expediteur);
        msg.setDestinataire(destinataire);
        msg.setContenu(contenu);
        msg.setDate(java.time.LocalDateTime.now());
        messagePriveRepository.save(msg);
    }

    public List<MessagePrive> getDerniersMessagesReçus(User user) {
        return messagePriveRepository.findTop5ByDestinataireOrderByDateDesc(user);
    }

    public List<MessagePrive> getConversation(User utilisateur1, User utilisateur2) {
        return messagePriveRepository.findByExpediteurAndDestinataireOrViceVersa(utilisateur1, utilisateur2);
    }
}
