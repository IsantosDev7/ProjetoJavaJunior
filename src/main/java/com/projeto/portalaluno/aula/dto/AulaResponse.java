package com.projeto.portalaluno.aula.dto;

import com.projeto.portalaluno.aula.roles.Modalidade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AulaResponse {

    private String titulo;
    private Modalidade modalidade;
    private Integer duracao;
    private UUID alunoId;
    private LocalDateTime dataHoraAula;

}
