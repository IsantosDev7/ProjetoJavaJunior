package com.example.portalaluno.aula;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AulaRepository extends JpaRepository<Aula, UUID> {
    List<Aula> findByProfessorId(UUID id);
    List<Aula> findByDataHoraAula(OffsetDateTime dataHoraAula);
    Optional<Aula> findById(UUID uuid);

}
