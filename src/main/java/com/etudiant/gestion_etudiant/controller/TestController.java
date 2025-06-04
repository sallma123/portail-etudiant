package com.etudiant.gestion_etudiant.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    // ✅ Route protégée simple
    @GetMapping("/test")
    public String testSecuredRoute() {
        return "✅ Accès autorisé avec token JWT";
    }

    // ✅ Voir l'utilisateur connecté
    @GetMapping("/me")
    public String getConnectedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Connecté en tant que : " + auth.getName();
    }

    // ✅ Route réservée à l'admin
    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "👑 Bienvenue Admin !";
    }
}
