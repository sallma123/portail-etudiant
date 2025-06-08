package com.etudiant.gestion_etudiant.config;

import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;

@ControllerAdvice
public class GlobalUserInfoAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void injectUserInfo(Authentication authentication, Model model) {
        if (authentication != null) {
            // 1. Ajouter les rôles
            Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
            model.addAttribute("roles", roles);

            // 2. Ajouter le nom complet de l'utilisateur
            String email = authentication.getName();
            userRepository.findByEmail(email).ifPresent(user -> {
                String fullName = user.getPrenom() + " " + user.getNom();
                model.addAttribute("fullName", fullName);
            });
        }
    }
}
