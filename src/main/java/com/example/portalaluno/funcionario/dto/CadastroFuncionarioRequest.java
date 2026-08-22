package com.example.portalaluno.funcionario.dto;

import java.util.List;

public class CadastroFuncionarioRequest {

    private FuncionarioRequest funcionario;
    private List<String> cargos;

    public FuncionarioRequest getFuncionario() {return funcionario;}
    public void setFuncionario(FuncionarioRequest funcionario) {this.funcionario = funcionario;}

    public List<String> getCargos() {return cargos;}
    public void setCargos(List<String> cargos) {this.cargos = cargos;}

}
