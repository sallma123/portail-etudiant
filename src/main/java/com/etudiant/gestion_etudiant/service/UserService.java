package com.etudiant.gestion_etudiant.service;

import com.etudiant.gestion_etudiant.entity.User;

public interface UserService {
    User findByEmail(String email);
}
