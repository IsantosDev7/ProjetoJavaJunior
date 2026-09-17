package com.projeto.portalaluno.solicitacao.dto;

import com.projeto.portalaluno.solicitacao.status.SolicitacaoStatus;
import com.projeto.portalaluno.solicitacao.status.SolicitacaoTipo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SolicitacaoResponse {

    private UUID id;
    private SolicitacaoTipo tipo;
    private String nomeSolicitante;
    private UUID funcionarioAlvoId;
    private String motivo;
    private SolicitacaoStatus status;
    private LocalDateTime createdAt;

}
