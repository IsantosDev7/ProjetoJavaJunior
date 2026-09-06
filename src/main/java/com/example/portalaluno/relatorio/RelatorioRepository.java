package com.example.portalaluno.relatorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RelatorioRepository extends JpaRepository<Relatorio, UUID> {
    boolean existsByAulaId(UUID aulaId);
    Page<Relatorio> findByProfessorId(UUID professorId, Pageable pageable);
    Page<Relatorio> findByProfessorIdAndAulaTituloContainingIgnoreCase(UUID professorId, String titulo, Pageable pageable);
    Page<Relatorio> findByAulaAlunoId(UUID alunoId, Pageable pageable);
    Page<Relatorio> findByAulaAlunoIdAndAulaTituloContainingIgnoreCase(UUID alunoId, String titulo, Pageable pageable);
    Page<Relatorio> findByProfessorNameContainingIgnoreCase(String nome, Pageable pageable);
    Page<Relatorio> findByAulaAlunoNameContainingIgnoreCase(String nome, Pageable pageable);
}

