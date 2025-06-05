package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Reponse;
import com.etudiant.gestion_etudiant.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReponseRepository extends JpaRepository<Reponse, Long> {
    List<Reponse> findByQuestion(Question question);
    void deleteByQuestionId(Long questionId);

}
