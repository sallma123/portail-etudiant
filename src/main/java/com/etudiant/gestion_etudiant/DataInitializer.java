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

            // ❌ Supprimer anciens rôles incorrects si présents
            roleRepo.findByName("ADMIN").ifPresent(roleRepo::delete);
            roleRepo.findByName("ETUDIANT").ifPresent(roleRepo::delete);
            roleRepo.findByName("PROF").ifPresent(roleRepo::delete);

            // ✅ Créer les rôles corrects s'ils n'existent pas
            if (roleRepo.findByName("ROLE_ADMIN").isEmpty())
                roleRepo.save(new Role(null, "ROLE_ADMIN"));
            if (roleRepo.findByName("ROLE_ETUDIANT").isEmpty())
                roleRepo.save(new Role(null, "ROLE_ETUDIANT"));
            if (roleRepo.findByName("ROLE_ENSEIGNANT").isEmpty())
                roleRepo.save(new Role(null, "ROLE_ENSEIGNANT"));

            // ✅ Créer un utilisateur admin si inexistant
            if (userRepo.findByEmail("salmasedrati13@gmail.com").isEmpty()) {
                Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow();

                User admin = new User();
                admin.setEmail("salmasedrati13@gmail.com");
                admin.setPassword(encoder.encode("1234"));
                admin.setNom("Admin");
                admin.setPrenom("Portail");
                admin.setEnabled(true);
                admin.setRole(adminRole);

                userRepo.save(admin);
                System.out.println("✅ Admin créé : salmasedrati13@gmail.com / 1234");
            }
        };
    }
}
