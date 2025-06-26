
package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.MessagePriveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        msg.setLu(false);
        messagePriveRepository.save(msg);
    }

    public List<MessagePrive> getDerniersMessagesReçus(User user) {
        return messagePriveRepository.findTop5ByDestinataireOrderByDateDesc(user);
    }

    public List<MessagePrive> getConversation(User utilisateur1, User utilisateur2) {
        return messagePriveRepository.findByExpediteurAndDestinataireOrViceVersa(utilisateur1, utilisateur2);
    }

    public String getDernierMessageEntre(User utilisateur1, User utilisateur2) {
        Optional<MessagePrive> dernier = messagePriveRepository
                .findTopByExpediteurAndDestinataireOrDestinataireAndExpediteurOrderByDateDesc(
                        utilisateur1, utilisateur2, utilisateur1, utilisateur2);

        return dernier.map(MessagePrive::getContenu).orElse("");
    }
}
