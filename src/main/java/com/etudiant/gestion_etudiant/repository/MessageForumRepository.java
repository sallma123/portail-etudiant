package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.MessageForum;
import com.etudiant.gestion_etudiant.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageForumRepository extends JpaRepository<MessageForum, Long> {
    List<MessageForum> findByCoursOrderByDateAsc(Cours cours);
}
