package com.example.portalaluno.aula.dto;


import com.example.portalaluno.aula.roles.Modalidade;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;
@Setter
@Getter
public class AulaRequest {

    private String titulo;
    private Modalidade modalidade;
    private Integer duracao;
    private UUID alunoId;
    private OffsetDateTime dataHoraAula;

}
