package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.Question;
import com.etudiant.gestion_etudiant.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);
}
