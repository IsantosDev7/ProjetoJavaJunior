package com.example.portalaluno.relatorio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RelatorioResponse {

    private UUID aulaId;
    private String texto;
    private UUID professorId;
    private Boolean confirmadoLeitura;
    private LocalDateTime createdAt;

}
