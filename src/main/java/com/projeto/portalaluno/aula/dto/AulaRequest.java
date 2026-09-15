package com.projeto.portalaluno.aula.dto;


import com.projeto.portalaluno.aula.roles.Modalidade;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class AulaRequest {

    @NotBlank
    private String titulo;

    @NotNull
    private Modalidade modalidade;

    @NotNull
    private Integer duracao;

    @NotNull
    private UUID alunoId;

    @Future
    @NotNull
    private LocalDateTime dataHoraAula;

}
