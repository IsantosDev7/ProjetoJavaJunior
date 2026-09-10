package com.example.portalaluno.cronograma.dto;

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
public class CronogramaResponse {

    private LocalDate semanaInicio;
    private LocalDate semanaFim;
    private List<DiaResponse> dias;


}
