package com.example.portalaluno.funcionario.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CadastroFuncionarioRequest {

    private FuncionarioRequest funcionario;
    private List<String> cargos;

}
