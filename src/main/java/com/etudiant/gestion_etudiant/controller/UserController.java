package com.etudiant.gestion_etudiant.controller;

import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.entity.Role;
import com.etudiant.gestion_etudiant.repository.RoleRepository;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Liste des utilisateurs
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "user-list";
    }

    // Formulaire d'ajout
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form";
    }

    // Enregistrement d'un nouveau user
    @PostMapping
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("roleId") Long roleId,
                           Model model) {

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Cet email est déjà utilisé.");
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
            return "user-form";
        }

        Role role = roleRepository.findById(roleId).orElse(null);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // ➤ Formulaire de modification
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form";
    }

    // ➤ Mise à jour du user
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam("roleId") Long roleId) {

        User existing = userRepository.findById(id).orElseThrow();

        existing.setNom(user.getNom());
        existing.setPrenom(user.getPrenom());
        existing.setEmail(user.getEmail());

        Role role = roleRepository.findById(roleId).orElse(null);
        existing.setRole(role);

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(existing);
        return "redirect:/admin/users";
    }

    // ➤ Suppression
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
