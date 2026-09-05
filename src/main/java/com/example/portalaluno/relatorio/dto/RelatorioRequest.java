package com.example.portalaluno.relatorio.dto;

import com.example.portalaluno.aula.roles.Modalidade;
import com.example.portalaluno.funcionario.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RelatorioRequest {

    private UUID aulaid;
    private String texto;
    private UUID professorId;

}
