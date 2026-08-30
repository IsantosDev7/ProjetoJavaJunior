package com.example.portalaluno.aluno;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import com.example.portalaluno.aluno.dto.AlunoResponse;
import com.example.portalaluno.aula.Aula;
import com.example.portalaluno.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    List<Aluno> findByNameContainingIgnoreCase(String name);
    Optional<Aluno> findById(UUID id);
    Optional<Aluno> findByUsuario(User usuario);
    List<Aluno> findByAlunoStatusMatricula(AlunoStatusMatricula status);
}