package com.example.portalaluno.aula;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AulaRepository extends JpaRepository<Aula, UUID> {
    List<Aula> findByProfessorId(UUID id);
    List<Aula> finByDataHoraAula(OffsetDateTime dataHoraAula);
}
