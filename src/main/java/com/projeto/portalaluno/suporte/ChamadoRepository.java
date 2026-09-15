package com.projeto.portalaluno.suporte;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {
    Page<Chamado> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
