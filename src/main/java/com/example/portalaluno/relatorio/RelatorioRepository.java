package com.example.portalaluno.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RelatorioRepository extends JpaRepository<Relatorio, UUID> {
    boolean existsByAulaId(UUID aulaId);
}
