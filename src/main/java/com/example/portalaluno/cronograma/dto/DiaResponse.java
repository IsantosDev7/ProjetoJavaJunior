package com.example.portalaluno.cronograma.dto;

import com.example.portalaluno.aula.dto.AulaResumoResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaResponse {

    private LocalDate data;
    private String nomeDia;
    private List<AulaResumoResponse> aulas;
}
