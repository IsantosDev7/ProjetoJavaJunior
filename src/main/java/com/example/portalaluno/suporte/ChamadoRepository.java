package com.example.portalaluno.suporte;

import com.example.portalaluno.aluno.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {
    Optional<Chamado> findByAlunoId(Aluno alunoId);
}
