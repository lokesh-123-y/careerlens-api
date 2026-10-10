package com.careerlens.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerlens.api.entity.Resume;
import com.careerlens.api.entity.User;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByUser(User user);

}