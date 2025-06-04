package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.dto.AuthRequest;
import com.etudiant.gestion_etudiant.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        try {
            System.out.println("Tentative de connexion : " + request.getEmail());

            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword());
            authManager.authenticate(authToken);

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    request.getEmail(), request.getPassword(), authToken.getAuthorities());

            return jwtUtils.generateToken(userDetails);
        } catch (AuthenticationException e) {
            System.out.println("Erreur auth : " + e.getMessage());
            return "Erreur de connexion : " + e.getMessage();
        }
    }

}
