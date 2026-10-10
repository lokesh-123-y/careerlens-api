package com.careerlens.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerlens.api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}