package com.example.portalaluno.suporte.dto;

import com.example.portalaluno.suporte.ChamadoPrioridade;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ChamadoResponse {

    private String titulo;
    private String descricao;
    private UUID alunoId;
    private ChamadoPrioridade prioridade;
    private LocalDateTime createdAt;  // ← Deveria retornar
    private boolean resolvido;

}
