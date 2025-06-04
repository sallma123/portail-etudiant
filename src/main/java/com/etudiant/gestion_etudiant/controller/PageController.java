package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@Controller
public class PageController {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public PageController(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            String msg = error.equals("unauthorized") ? "Accès non autorisé." : "Identifiants invalides !";
            model.addAttribute("error", msg);
        }
        return "login";
    }


    @GetMapping("/admin/dashboard")
    public String adminPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("ADMIN".equals(role)) {
            return "admin";
        }
        return "redirect:/login?error=unauthorized";
    }

    @GetMapping("/etudiant/dashboard")
    public String etudiantPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("ETUDIANT".equals(role)) {
            return "etudiant";
        }
        return "redirect:/login?error=unauthorized";
    }

    @GetMapping("/enseignant/dashboard")
    public String enseignantPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("ENSEIGNANT".equals(role)) {
            return "enseignant";
        }
        return "redirect:/login?error=unauthorized";
    }
    @GetMapping("/redirect-after-login")
    public String redirectAfterLogin(HttpSession session, Authentication auth) {
        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElse(null);

        if (user != null) {
            session.setAttribute("role", user.getRole().getName());
            switch (user.getRole().getName()) {
                case "ADMIN": return "redirect:/admin/dashboard";
                case "ETUDIANT": return "redirect:/etudiant/dashboard";
                case "ENSEIGNANT": return "redirect:/enseignant/dashboard";
            }
        }
        return "redirect:/login?error=true";
    }

}
