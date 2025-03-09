package com.itgirls.bank_system.repository;

import com.itgirls.bank_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);
}