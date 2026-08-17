package com.example.portalaluno.aluno.dto;

import com.example.portalaluno.responsavel.Responsavel;
import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;

public class CadastroAlunoRequest {
    private AlunoRequest aluno;
    private ResponsavelRequest responsavel;

    public AlunoRequest getAluno() {
        return aluno;
    }
    public void setAluno(AlunoRequest aluno) {
        this.aluno = aluno;
    }
    public ResponsavelRequest getResponsavel() {
        return responsavel;
    }
    public void setResponsavel(ResponsavelRequest responsavel) {
        this.responsavel = responsavel;
    }
}