package com.projeto.portalaluno.solicitacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {
    Page<Solicitacao> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
