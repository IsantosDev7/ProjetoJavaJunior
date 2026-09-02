package com.example.portalaluno.aula.dto;


import com.example.portalaluno.aula.roles.Modalidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;
@Setter
@Getter
public class AulaRequest {

    @NotBlank
    private String titulo;

    @NotBlank
    private Modalidade modalidade;

    @NotNull
    private Integer duracao;

    @NotNull
    private UUID alunoId;

    @Past
    private OffsetDateTime dataHoraAula;

}
