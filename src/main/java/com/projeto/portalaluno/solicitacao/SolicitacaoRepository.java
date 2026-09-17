package com.projeto.portalaluno.solicitacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {


}
