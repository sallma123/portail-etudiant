package com.etudiant.gestion_etudiant.repository;

import com.etudiant.gestion_etudiant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken); // pour la réinitialisation
    long countByRole_Name(String roleName);
    List<User> findByRole_Name(String roleName);


}
