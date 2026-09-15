package com.projeto.portalaluno.aluno;

import java.util.UUID;
import java.util.Optional;

import com.projeto.portalaluno.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Page<Aluno> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Aluno> findById(UUID id);
    Optional<Aluno> findByUsuario(User usuario);
}