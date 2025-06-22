package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.MessageForum;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.MessageForumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumService {

    private final MessageForumRepository messageForumRepository;

    public ForumService(MessageForumRepository messageForumRepository) {
        this.messageForumRepository = messageForumRepository;
    }

    // ✅ Récupérer tous les messages du forum pour un cours donné
    public List<MessageForum> getMessagesParCours(Cours cours) {
        return messageForumRepository.findByCoursOrderByDateAsc(cours);
    }

    // ✅ Ajouter un nouveau message dans le forum
    public void ajouterMessage(MessageForum message) {
        message.setDate(LocalDateTime.now());
        messageForumRepository.save(message);
    }

    // ✅ Supprimer un message par son auteur (utilisé dans l'interface utilisateur)
    public void supprimerMessage(Long id, User userConnecte) {
        MessageForum msg = messageForumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        if (!msg.getAuteur().getId().equals(userConnecte.getId())) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres messages");
        }

        messageForumRepository.delete(msg);
    }

    // ✅ Supprimer un message (utilisé en interne par l'admin ou pour suppression du cours)
    public void supprimerMessageSansVérif(Long id) {
        messageForumRepository.findById(id).ifPresent(messageForumRepository::delete);
    }

    // ✅ Supprimer tous les messages associés à un cours (appelé lors de suppression de cours)
    public void supprimerMessagesParCours(Cours cours) {
        List<MessageForum> messages = getMessagesParCours(cours);
        for (MessageForum msg : messages) {
            supprimerMessageSansVérif(msg.getId());
        }
    }
}
