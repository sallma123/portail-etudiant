package com.etudiant.gestion_etudiant;

import com.etudiant.gestion_etudiant.entity.Role;
import com.etudiant.gestion_etudiant.entity.User;
import com.etudiant.gestion_etudiant.repository.RoleRepository;
import com.etudiant.gestion_etudiant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            // 🔁 Création des rôles si non existants
            if (roleRepo.findByName("ADMIN").isEmpty()) {
                roleRepo.save(new Role(null, "ADMIN"));
                roleRepo.save(new Role(null, "ETUDIANT"));
                roleRepo.save(new Role(null, "PROF"));
            }

            // 🔁 Création d'un utilisateur admin
            if (userRepo.findByEmail("salmasedrati13@gmail.com").isEmpty()) {
                Role adminRole = roleRepo.findByName("ADMIN").get();

                User admin = new User();
                admin.setEmail("salmasedrati13@gmail.com");
                admin.setPassword(encoder.encode("1234")); // Mot de passe encodé !
                admin.setNom("Admin");
                admin.setPrenom("Portail");
                admin.setEnabled(true);
                admin.setRole(adminRole);

                userRepo.save(admin);
                System.out.println("✅ Admin ajouté avec email: admin@example.com et mot de passe: 1234");
            }
        };
    }
}
