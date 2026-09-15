package com.projeto.portalaluno.aula;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AulaRepository extends JpaRepository<Aula, UUID> {
    Page<Aula> findByProfessorId(UUID id, Pageable pageable);
    Page<Aula> findByAlunoId(UUID id, Pageable pageable);
    Optional<Aula> findById(UUID uuid);
    List<Aula> findByDataHoraAulaBetween(LocalDateTime inicio, LocalDateTime fim);
}
