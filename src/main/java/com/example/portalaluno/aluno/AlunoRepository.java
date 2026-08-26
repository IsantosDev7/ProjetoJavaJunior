package com.example.portalaluno.aluno;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    List<Aluno> findByNameContainingIgnoreCase(String name);
    Optional<Aluno> findById(UUID id);
}