package com.projeto.portalaluno.aula.dto;

import com.projeto.portalaluno.aula.roles.Modalidade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AulaResumoResponse {
    private String titulo;
    private LocalTime horario;
    private String nomeProfessor;
    private String nomeAluno;
    private Modalidade modalidade;
}
