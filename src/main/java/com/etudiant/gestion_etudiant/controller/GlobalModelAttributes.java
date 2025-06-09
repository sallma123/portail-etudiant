// com.etudiant.gestion_etudiant.controller.GlobalModelAttributes.java
package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.MessagePrive;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import com.etudiant.gestion_etudiant.service.MessagerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagerieService messagerieService;

    @ModelAttribute("messagesRecents")
    public List<MessagePrive> chargerMessagesRecents(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return List.of();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return List.of();

        return messagerieService.getDerniersMessagesReçus(user);
    }
}
