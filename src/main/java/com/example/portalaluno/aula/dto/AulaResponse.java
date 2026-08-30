package com.example.portalaluno.aula.dto;

import com.example.portalaluno.aula.roles.Modalidade;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;
@Getter
@AllArgsConstructor
public class AulaResponse {

    private UUID id;
    private String titulo;
    private Modalidade modalidade;
    private Integer duracao;
    private UUID alunoId;
    private OffsetDateTime dataHoraAula;

}
