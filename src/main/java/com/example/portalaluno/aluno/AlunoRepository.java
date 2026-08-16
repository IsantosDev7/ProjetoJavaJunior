package com.example.portalaluno.aluno;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Optional<Aluno> findByEmail(String email);
    List<Aluno> findByNameContainingIgnoreCase(String nome);
}