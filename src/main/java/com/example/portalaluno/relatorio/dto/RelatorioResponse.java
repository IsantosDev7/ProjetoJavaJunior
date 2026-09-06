package com.example.portalaluno.relatorio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RelatorioResponse {

    private UUID aulaid;
    private String texto;
    private UUID professorId;
    private Boolean confirmadoLeitura;

}
