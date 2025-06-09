package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.Cours;
import com.etudiant.gestion_etudiant.entity.MessageForum;
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
}
